import {Route, Routes} from 'react-router-dom';
import UserContext from "./context/user-context";
import {useContext} from "react";
import Layout from "./layout/Layout";
import LoginPage from "./pages/LoginPage";
import MerchantList from "./pages/MerchantList";
import Dashboard from "./pages/Dashboard";
import RegisterPage from "./pages/RegisterPage";


function App() {


    const userContext = useContext(UserContext);
    return (
        userContext.loggedIn ?
            <Layout>
                <Routes>
                    <Route path="/" element={<Dashboard/>}/>
                    <Route path='/merchants' element={<MerchantList/>}/>
                </Routes>
            </Layout>
            :
            <Routes>
                <Route path="/" element={<LoginPage/>}/>
                <Route path='/register' element={<RegisterPage/>}/>
            </Routes>
    );
}

export default App;
