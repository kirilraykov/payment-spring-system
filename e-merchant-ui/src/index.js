import React from 'react';
import ReactDOM from 'react-dom/client';
import {BrowserRouter} from "react-router-dom";
import {UserContextProvider} from "./context/user-context";


import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <UserContextProvider>
        <BrowserRouter>
            <App/>
        </BrowserRouter>
    </UserContextProvider>
);
