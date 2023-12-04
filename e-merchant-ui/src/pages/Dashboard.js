import {useContext, useEffect} from "react";
import UserContext from "../context/user-context";
import MyTransactions from "../components/MyTransactions";

function Dashboard() {

    const userContext = useContext(UserContext);

    useEffect(() => {

    }, []);

    return (
        <div>
            <h3>Welcome, {userContext.user.sub}!</h3>
            {userContext.user.userType.toLowerCase() === 'merchant' ?
                <MyTransactions merchantId={userContext.user.userId}/> : ""}
        </div>
    )
}

export default Dashboard;
