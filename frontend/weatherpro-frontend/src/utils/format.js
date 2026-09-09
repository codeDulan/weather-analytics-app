export function formatTemp(celcius) {
    if (celcius == null || Number.isNaN(celcius)) return '_';
    return `${Math.round(celcius)}°C`;
}

export function formatScore(score) {
    if (score == null || Number.isNaN(score)) return '_';
    return Math.round(score);
}

export function ownIconUrl(iconCode, size = '2x') {
    if (!iconCode) return null;
    return `https://openweathermap.org/img/wn/${iconCode}@${size}.png`;
}

export function titleCase(text) {
    if (!text) return '';
    return text.replace(/\b\w/g, (char) => char.toUpperCase());
}

export function formatObservedAt(isoInstant) {
    if (!isoInstant) return '';
    const date = new Date(isoInstant);
    if (Number.isNaN(date.getTime())) return '';
    return date.toLocaleString(undefined, {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    });
}

export function backgroundBucket(rank, totalCities) {
    if (rank === 1) return 'spring';
    if(totalCities > 1 && rank === totalCities) return 'desert';
    return 'nature';
}