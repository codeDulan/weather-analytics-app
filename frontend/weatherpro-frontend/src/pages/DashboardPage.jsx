import {useMemo, useState } from 'react' 
import { Col, Container, Row } from 'react-bootstrap'

import CityDetailCard from '../component/CityDetailCard'
import CityRankList from '../component/CityRankList'
import { useRankedCities } from '../hooks/useRankedCities'

export default function DashboardPage() {
    const { cities, loading, error } = useRankedCities();
    const [selectedCityCode, setSelectedCityCode] = useState(null);

    const selectedCity = useMemo(() => {
        if(cities.length === 0) return null
        return cities.find((c) => c.cityCode === selectedCityCode) ?? cities[0]
    }, [cities, selectedCityCode])

    return (
        <Container className="py-4 py-md-5">
            <header className="text-center mb-4 mb-md-5">
                <h1 className="fw-bold display-5">
                    Weather<span className="text-primary">Pro</span>
                </h1>
                <p className="wp-text-muted mb-0">
                    Cities ranked from most to least comfortable, right now
                </p>
            </header>

            {loading && <p className="text-center">Loading cities…</p>}
            {error && (
                <p className="text-center text-warning">
                Could not load cities: {error.message}
                </p>
            )}

            {!loading && !error && selectedCity && (
                <Row className="g-4">
                    <Col lg={7}>
                    <CityDetailCard city={selectedCity} totalCities={cities.length} />
                    </Col>
                    <Col lg={5}>
                    <CityRankList
                        cities={cities} 
                        selectedCityCode={selectedCityCode} 
                        onSelect={setSelectedCityCode} />
                    </Col>
                </Row>
            )}

        </Container>
    )
}