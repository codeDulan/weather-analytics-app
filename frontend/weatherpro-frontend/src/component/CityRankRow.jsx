import { formatScore, formatTemp, ownIconUrl, titleCase } from '../utils/format'

export default function CityRankRow({ city, active, onSelect}) {
    const iconUrl = ownIconUrl(city.iconCode)

    return (
        <button
        type="button"
        aria-pressed={active}
        onClick={() => onSelect(city.cityCode)}
        className={`wp-rank-row ${active ? 'wp-rank-row--active' : ''}`}>
            <span className="wp-rank-row__rank">{city.rank}</span>

            {iconUrl && <img src={iconUrl} alt="" width={44} height={44} />}

            <span className="wp-rank-row__body">
                <span className="wp-rank-row__name">
                    {city.cityName}
                    {city.country ? `, ${city.country}` : ''}
                </span>
                <span className="wp_rank_row__desc">{titleCase(city.description)}</span>
            </span>

            <span className="wp-rank-row__stats">
                <span className="wp-rank-row__score">{formatScore(city.comfortScore)}</span>
                <span className="wp-rank-row__temp">{formatTemp(city.temperatureC)}</span>
            </span>

        </button>

    )
}