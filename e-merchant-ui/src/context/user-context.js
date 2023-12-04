import axios from 'axios';
import {createContext, useEffect, useState} from 'react';
import {useCookies} from 'react-cookie'

const UserContext = createContext({
    loggedIn: false,
    user: {},
    login: (email, password) => {
    },
    logout: () => {
    },
    getBackendUrl: () => {
    },
});

export function UserContextProvider(props) {

    const [profile, setProfile] = useState(
        {
            loggedIn: false,
            user: {},
            login: (email, password) => {
            },
            logout: () => {
            },
            getBackendUrl: () => {
            },
        }
    );

    const [accessToken, setAccessToken] = useCookies(['bearer_token'])

    if (accessToken.bearer_token) {

        axios.defaults.headers.common['Authorization'] = 'Bearer ' + accessToken.bearer_token;
        axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';

    }

    function setToken(token) {

        console.log("SET DEFAULT BEARER " + token);
        let expires = new Date();
        expires.setTime(expires.getTime() + (60 * 60 * 24 * 1000));
        setAccessToken('bearer_token', token, {path: '/', expires});
        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
        axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';

    }

    function login(username, password) {

        axios.post(getBackendUrl() + '/auth/login', {username, password})
            .then((response) => {
                console.log(response.data);
                setToken(response.data);

            }).catch((error) => {
            return false;
        });

        reloadUser();
    }

    function logout() {
        let expires = new Date();
        setAccessToken('bearer_token', null, {path: '/', expires});
        reloadUser();
        window.location.href = '/';

    }

    function reloadUser() {
        if (accessToken.bearer_token) {
            let user = JSON.parse(atob(accessToken.bearer_token.split('.')[1]));

            console.log("USER INFO FROM TOKEN:");
            console.log(user);

            if ((user.exp * 1000) > new Date().getTime() || 1 === 1) {
                setProfile((prevState) => ({
                    loggedIn: true,
                    user: user,
                    login: login,
                    logout: logout,
                    getBackendUrl: getBackendUrl,
                }));
            } else {
                setProfile((prevState) => ({
                    loggedIn: false,
                    user: {},
                    login: login,
                    logout: logout,
                    getBackendUrl: getBackendUrl,
                }));
            }

        } else {
            setProfile((prevState) => ({
                loggedIn: false,
                user: {},
                login: login,
                logout: logout,
                getBackendUrl: getBackendUrl,
            }));
        }
    }

    function getBackendUrl() {
        return "http://localhost:8080/api";
    }

    useEffect(() => {
        reloadUser();
    }, [accessToken, setAccessToken]);

    return <UserContext.Provider value={profile}>
        {props.children}
    </UserContext.Provider>
}

export default UserContext;
