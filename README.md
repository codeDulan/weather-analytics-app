# weather-analytics-app

A secure weather analytics application. It reads a list of cities, fetches current
weather for each from OpenWeatherMap, computes a custom **Comfort Index** (0–100)
on the server, and presents the cities ranked from most to least comfortable.
Access is protected with Auth0 (login, email MFA, whitelist-only, no public
sign-ups).

- **Backend** — Java 21, Spring Boot, Maven. REST API + Comfort Index + caching + JWT validation.
- **Frontend** — React 19 + Vite, react-bootstrap, Auth0 SPA SDK.

## Auth0 tenant setup

You need **three values**: a tenant **domain**, an SPA **client ID**, and an API
**audience**.

### 1. Create the API (the audience)

**Applications → APIs → Create API**

- Name: `Weather Analytics API`
- Identifier: `https://weather-analytics-api` — a fixed label, it does not have to resolve. **This is `AUTH0_AUDIENCE`.**
- Signing algorithm: `RS256`

### 2. Create the SPA application

**Applications → Applications → Create Application → Single Page Web Applications**

In its **Settings**:

- Note the **Domain** (`AUTH0_DOMAIN`) and **Client ID** (`VITE_AUTH0_CLIENT_ID`).
- **Allowed Callback URLs:** `http://localhost:5173`
- **Allowed Logout URLs:** `http://localhost:5173`
- **Allowed Web Origins:** `http://localhost:5173`

### 3. Enable email MFA

**Security → Multi-factor Auth**

- Enable the **Email** factor.
- Set **Require Multi-factor Authentication** to **Always**.

### 4. Disable public sign-ups

**Authentication → Database → `Username-Password-Authentication` → Settings**

- Turn on **Disable Sign Ups**.

### 5. Whitelist Action

**Actions → Library → Create Action** — trigger *Login / Post Login*:

```js
exports.onExecutePostLogin = async (event, api) => {
  const allowlist = (event.secrets.ALLOWLIST || '')
    .split(',')
    .map((e) => e.trim().toLowerCase())
    .filter(Boolean);

  const email = (event.user.email || '').toLowerCase();
  if (!allowlist.includes(email)) {
    api.access.deny('Your account is not permitted to access this application.');
  }
};
```

Add a **secret** `ALLOWLIST` = `careers@fidenz.com,<your-email>`, **Deploy**, then
add the Action to the **Login** flow (**Actions → Triggers → post-login**).

### 6. Create users

**User Management → Users → Create User** (connection `Username-Password-Authentication`):

- `careers@fidenz.com` / `Pass#fidenz`
- one with your own email (you need the inbox for the MFA code)


## Running the backend

From the repository root:

```bash
AUTH0_DOMAIN=your-tenant.us.auth0.com \
AUTH0_AUDIENCE=https://weather-analytics-api \
OPENWEATHER_API_KEY=your_openweathermap_key \
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. It **fails fast on startup** if any of
these is missing.

| Environment variable | Required | Default | Meaning |
|---|---|---|---|
| `OPENWEATHER_API_KEY` | yes | — | OpenWeatherMap `appid` |
| `AUTH0_DOMAIN` | yes | — | tenant host, e.g. `dev-abc.us.auth0.com` |
| `AUTH0_AUDIENCE` | yes | — | must equal the frontend's `VITE_AUTH0_AUDIENCE` |
| `APP_CORS_ALLOWED_ORIGINS` | no | `http://localhost:5173` | comma-separated frontend origins |

Other settings (cache TTLs, request timeout, cities file, units) live in
[`application.properties`](src/main/resources/application.properties) and can be
overridden the same way.

Build a jar: `./mvnw clean package` → `target/weather-analytics-0.0.1-SNAPSHOT.jar`.

---

## Running the frontend

```bash
cd frontend
cp .env.example .env      # then edit .env with your real Auth0 values
npm install
npm run dev               # http://localhost:5173
```

`frontend/.env`:

```
VITE_API_BASE_URL=/api
VITE_AUTH0_DOMAIN=your-tenant.us.auth0.com
VITE_AUTH0_CLIENT_ID=your-spa-client-id
VITE_AUTH0_AUDIENCE=https://weather-analytics-api
```

In development, Vite proxies `/api` to `http://localhost:8080` (see
`vite.config.js`), so the browser makes same-origin requests. Production build:
`npm run build` → `frontend/dist/`.

## The Comfort Index formula

**Goal:** one number, 0–100, for how pleasant it feels to be outside right now.
100 = ideal, 0 = miserable. Computed entirely in the backend
([`ComfortIndexService`](src/main/java/com/fidenz/weather/service/ComfortIndexService.java)).

The weather API gives values in different units (°C, %, m/s), so they can't be
added directly. The formula works in two moves.

### Move 1 — grade each parameter 0–100 (100 = ideal)

**Temperature** (uses *feels-like*, which already folds in wind-chill / heat-index):

```
tempScore = 100 − 5.5 × |feelsLikeC − 22|            (clamped to 0..100)
```

22 °C scores 100; every degree away costs 5.5 points; ~4 °C or ~40 °C reaches 0.

**Humidity** — flat comfortable band 30–55 %, linear falloff outside it:

```
h < 30 :  100 − 1.5 × (30 − h)      (too dry)
30–55  :  100
h > 55 :  100 − 2.0 × (h − 55)      (too humid)          (clamped)
```

Humid is penalised harder than dry (2.0 vs 1.5) because mugginess stops sweat
evaporating — it feels worse than dry air at the same temperature.

**Wind** — a light breeze is fine, strong wind is not:

```
wind ≤ 3 m/s :  100
wind > 3 m/s :  100 − 8 × (wind − 3)                     (clamped; ~15.5 m/s → 0)
```

**Sky (cloudiness)** — a gentle mood factor, never painful:

```
skyScore = 100 − 0.5 × cloudiness%                       (fully overcast → 50, the floor)
```

### Move 2 — weighted sum

```
comfort = 0.55 × tempScore
        + 0.25 × humidityScore
        + 0.15 × windScore
        + 0.05 × skyScore
```

Weights sum to 1.0, so the result is already 0–100. Rounded to one decimal.
Cities are then sorted by `comfort` descending (ties broken by city name) and
assigned a 1-based rank.

### Reasoning behind the weights

| Parameter | Weight | Why |
|---|---:|---|
| Temperature | **0.55** | Dominant. If it's −5 °C or 42 °C the day is ruined regardless of everything else, so it is more than half the score by design. |
| Humidity | **0.25** | The strongest *modifier* of how temperature feels — 30 °C dry vs 30 °C humid are very different experiences. |
| Wind | **0.15** | Neutral most of the time; only matters at the extremes (dead calm vs gale), hence a smaller weight but a steep penalty slope. |
| Cloudiness | **0.05** | Real but minor. Included to use ≥ 3 parameters meaningfully and to break ties between otherwise-identical cities. |

---

## Cache design

Server-side, in-process, using **Caffeine**
([`CacheConfig`](src/main/java/com/fidenz/weather/config/CacheConfig.java)).
Two independent caches:

| Cache | Key | Value | TTL | Max size |
|---|---|---|---|---|
| `rawWeather` | city code | normalised `Weather` for one city (one OpenWeatherMap call) | `openweather.raw-cache-ttl` (5 min) | 500 |
| `rankedCities` | `"all"` | the whole scored + ranked list | `openweather.processed-cache-ttl` (5 min) | 1 |

- **Two caches, not one.** The brief asks for raw responses to be cached *and* the
  processed output to be cached separately. The raw cache means a partial failure
  (one city down) doesn't re-fetch the cities that already succeeded; the
  processed cache means a repeat dashboard load within 5 minutes does **zero**
  work — no HTTP, no scoring, no sorting.
- **`@Cacheable` at two layers.** `OpenWeatherClient.fetch(city)` populates
  `rawWeather`; `WeatherAnalyticsService.rankedCities()` populates `rankedCities`
  with `unless = "#result.isEmpty()"` so a total failure is never cached for
  5 minutes.
- **Statistics on.** Both caches call `.recordStats()`, surfaced by
  `GET /api/debug/cache` as hit/miss counts, hit rate, eviction count and a
  `HIT` / `MISS` / `EMPTY` status.
- **Why in-process.** Simple, zero infrastructure, correct for a single instance.
  A multi-instance deployment would swap in Redis via `spring-boot-starter-data-redis`
  without touching a single annotated method.

---