let accessTokenGetter = null;

export function setAccessTokenGetter(getter) {
    accessTokenGetter = getter;
}

export async function getAccessToken() {
    if(!accessTokenGetter) {
        return null;
    }

    try{
        return await accessTokenGetter();
    }catch {
        return null;
    }
}