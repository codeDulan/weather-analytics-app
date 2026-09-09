import { backgroundBucket, formatObservedAt, formatScore, formatTemp, ownIconUrl, titleCase} from '../utils/format'

export default function CityDetailsCard({ city, totalCities }) {
    const bucket = backgroundBucket(city.rank, totalCities)
    const iconUrl = ownIconUrl(city.iconCode, '4x')

    const details = [
        ['Feels like', formatTemp(city.feelsLikeC)],
        ['Humidity', `${Math.round(city.humidityPercent)}%`],
        ['Wind', `${city.windSpeedMs.toFixed(1)} m/s`],
        ['Cloudiness', `${Math.round(city.cloudinessPercent)}%`],
    ]

    return (
        <section className={`wp-glass wp-detail wp-vibe--${bucket}`}>
            <div className="wp-detail__head">
                <div>
                    <h2 className="wp-details__city">
                        {city.cityName}
                        {city.country ? `, ${city.country}` : ''}
                    </h2>
                    <p className="wp-detail__meta">
                        Rank #{city.rank} - {formatObservedAt(city.observedAt)}
                    </p>
                </div>
                <div className="wp-detail__score">
                    <span className="wp-detail__score-value">{formatScore(city.comfortScore)}</span>
                    <span className="wp-detail__score-label">Comfort</span>
                </div>
            </div>

            <div className="wp-detail__now">
                {iconUrl && <img src={iconUrl} alt="" width={110} height={110} />}
                <div>
                    <span className="wp-detail__temp">{formatTemp(city.temperatureC)}</span>
                    <span className="wp-detail__desc">{titleCase(city.description)}</span>
                </div>
            </div>

            <dl className="wp-detail__grid">
                {details.map(([label, value]) => (
                    <div key={label}>
                        <dt>{label}</dt>
                        <dd>{value}</dd>
                    </div>
                ))}
            </dl>

        </section>
    )
}