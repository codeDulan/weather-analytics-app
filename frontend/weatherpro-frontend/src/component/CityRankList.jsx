import CityRankRow from "./CityRankRow";

export default function CityRankList({ cities, selectedCityCode, onSelect}) {
    return (
        <section className="wp-glass wp-rank-list">
            <h2 className="wp-rank-list__title">City Ranking</h2>

            <div className="wp-rank-list__rows">
                {cities.map((city) => (
                    <CityRankRow
                    key={city.cityCode}
                    city={city}
                    active={city.cityCode === selectedCityCode}
                    onSelect={onSelect}
                    />
                ))}
            </div>
        </section>
    )
}