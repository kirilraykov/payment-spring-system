import {useContext} from 'react';
import UserContext from "../context/user-context";

function UserNavigation() {

    const userContext = useContext(UserContext);
    return (
        <div className='w-100'>
            <span><i className={"fa fa-user-circle"}/> {userContext.user.sub} <span
                style={{opacity: 0.4}}>{userContext.user.userEmail}</span>  (<span
                className={'text-info'}>{userContext.user.userType}</span>)</span>

            <button type='button' className='btn btn-dark btn-sm ms-2 pt-0 float-end me-4' onClick={userContext.logout}>
                <i className="fa fa-sign-out"/> Sing-out
            </button>
        </div>
    );
}

export default UserNavigation;
