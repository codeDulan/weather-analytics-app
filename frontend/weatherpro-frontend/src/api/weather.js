import { apiClient } from './client';

export async function fetchRankedCities() {
    const { data } = await apiClient.get('/cities');
    return data;
}

export async function fetchCacheStatus() {
    const { data } = await apiClient.get('/debug/cache');
    return data;
}