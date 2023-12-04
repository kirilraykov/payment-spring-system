import {useContext, useRef, useState} from "react";
import UserContext from "../context/user-context";
import {Link} from "react-router-dom";

function LoginPage(){

    const userContext =  useContext(UserContext);

    const [currentErrors, setCurrentErrors] = useState([]);
    const usernameRef = useRef();
    const passwordRef = useRef();

    function loginHandler(event){
        event.preventDefault();

        let username = usernameRef.current.value;
        let password = passwordRef.current.value;

        var errors = [];

        if(userContext.login(username, password)){
            console.log("LOGGED SUCCESSFULLY");
        }else{

            setTimeout(function(){
                errors.push("Invalid credentials");
                setCurrentErrors(errors);
            },2000);

        }

    }
    return (
        <div className='container-sm mt-5'>
            <div className='row'>
                <div className='col-lg-4 col-sm-12' />
                <div className='col-lg-4 col-sm-12'>
                    <div className='card shadow'>
                        <div className="card-header">Sign-in</div>
                        <div className='card-body'>
                            <form onSubmit={loginHandler}>
                                <div className="input-group mb-3 mt-3 text-center">


                                </div>
                                <div className="input-group mb-3">
                                    <span className="input-group-text" style={{width:'120px'}}><i className='fa fa-user me-2' /> Username</span>
                                    <input type="text" className="form-control" id='username' ref={usernameRef}  required/>
                                </div>
                                <div className="input-group mb-3">
                                    <span className="input-group-text" style={{width:'120px'}}><i className='fa fa-lock me-2' /> Password</span>
                                    <input type="password" className="form-control" id='password' placeholder='**********' ref={passwordRef}  required/>
                                </div>
                                <div className='w-100 mb-3 text-end'>
                                    <button type="submit" className="btn btn-primary btn-block mb-4"><i className='fa fa-sign-in' /> Sign in</button>
                                </div>

                                {currentErrors.length > 0 ?
                                    <div className="w-100 mt-3">
                                        <div className='alert alert-warning p-2'>
                                            {currentErrors.map((error)=><p key={error} className='p-0 m-0'><i className='fa fa-ban' /> {error}</p>)}
                                        </div>
                                    </div> : null }

                                    <p>
                                        You do not have an account ? <Link to={'/register'}>Create new account </Link>
                                    </p>

                            </form>
                        </div>
                    </div>
                </div>
                <div className='col-lg-4 col-sm-12' />
            </div>
        </div>
    )

}

export default LoginPage;
