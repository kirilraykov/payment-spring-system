import {NavLink} from "react-router-dom";
import {useContext} from "react";

import UserContext from "../context/user-context";

function SideBar() {
    const userContext = useContext(UserContext);

    return (
        <div className="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2 text-white min-vh-100">

            <h4 className={'mt-2'}><span className={'text-warning'}>M</span>ERCHANT CONTROL</h4>

            <ul className="nav nav-pills flex-column mb-sm-auto mb-0 align-items-sm-start w-100" id="menu">
                <li className="w-100">
                    <NavLink to={'/'} className="nav-link align-middle px-4 w-100 text-white">
                        <i className="fa fa-desktop me-2"/><span style={{paddingTop: '10px'}}>Dashboard</span>
                    </NavLink>
                </li>
                {userContext.user.userType.toLowerCase() === 'admin' ?
                    <li className="w-100">
                        <NavLink to={'/merchants'} className="nav-link align-middle px-4 w-100 text-white">
                            <i className='fas fa-users me-2'/><span style={{paddingTop: '10px'}}>Merchants</span>
                        </NavLink>
                    </li>
                    : ""}
            </ul>
        </div>
    );
}

export default SideBar;
