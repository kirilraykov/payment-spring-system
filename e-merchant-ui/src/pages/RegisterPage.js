import {Link} from "react-router-dom";
import {useContext, useRef, useState} from "react";
import UserContext from "../context/user-context";
import axios from "axios";

function RegisterPage() {
    const userContext = useContext(UserContext);

    const [currentErrors, setCurrentErrors] = useState([]);
    const [successMessage, setSuccessMessage] = useState(null);

    const emailRef = useRef();
    const usernameRef = useRef();
    const nameRef = useRef();
    const passwordRef = useRef();
    const passwordConfirmRef = useRef();
    const statusRef = useRef();
    const descriptionRef = useRef();

    const validateEmail = (email) => {
        return String(email)
            .toLowerCase()
            .match(
                /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            );
    };

    function registerHandler(event) {
        setCurrentErrors([]);
        event.preventDefault();
        let email = emailRef.current.value;
        let name = nameRef.current.value;
        let password = passwordRef.current.value;
        let password_confirmation = passwordConfirmRef.current.value;
        let username = usernameRef.current.value;
        let status = statusRef.current.value;
        let description = descriptionRef.current.value;
        var errors = [];


        if (password.length < 6) {
            errors.push("Password must be at least 6 characters long");
        } else {
            if (password !== password_confirmation) errors.push("Passwords does not match");
        }

        if (name.length < 4) errors.push("Name must be at least 4 characters long");
        if (username.length < 4) errors.push("Username must be at least 4 characters long");
        if (!validateEmail(email)) errors.push("Invalid email address");

        setCurrentErrors(errors);

        if (errors.length === 0) {
            let payload = {
                name,
                username,
                password,
                email,
                status,
                description,
                type: "merchant",
                totalTransactionSum: 0.00
            };

            axios.post(userContext.getBackendUrl() + "/auth/signup", payload)
                .then((response) => {

                    setSuccessMessage(response.data.message);
                }).catch((error) => {
                let additional_info = null;
                if (error.response.data.message) additional_info = error.response.data.message;
                setCurrentErrors(["Error while signing up." + additional_info]);
            });

        }

    }

    return (
        <div className='container-sm mt-5'>
            <div className={'row text-center'}>
                <div className='col-lg-2 col-sm-12'/>
                <div className='col-lg-6 col-sm-12'>
                    <div className="card shadow">
                        <div className="card-header">Sign up</div>
                        <div className="card-body">
                            {
                                !successMessage ?

                                    <form onSubmit={registerHandler}>
                                        <div className="input-group mb-3">
                                            <span className="input-group-text">Name</span>
                                            <input type="text" className="form-control" id='name' ref={nameRef}/>
                                        </div>
                                        <div className="input-group mb-3">
                                            <span className="input-group-text">Username</span>
                                            <input type="text" className="form-control" id='username'
                                                   ref={usernameRef}/>
                                        </div>

                                        <div className="input-group mb-3">
                                            <span className="input-group-text"><i className='fa fa-envelope me-2'/> Email</span>
                                            <input type="email" className="form-control" id='email' ref={emailRef}/>
                                        </div>

                                        <div className="input-group mb-3">
                                            <span className="input-group-text"><i className='fa fa-lock me-2'/> Password</span>
                                            <input type="password" className="form-control" id='password'
                                                   placeholder='**********' ref={passwordRef}/>
                                        </div>
                                        <div className="input-group mb-3">
                                            <span className="input-group-text"><i className='fa fa-lock me-2'/> Confirm Password</span>
                                            <input type="password" className="form-control" id='password_confirm'
                                                   placeholder='**********' ref={passwordConfirmRef}/>
                                        </div>

                                        <div className={'w-100'}>
                                            <h5>MERCHANT Details</h5>
                                        </div>
                                        <div className="input-group mb-3">
                                            <span className="input-group-text">Status</span>
                                            <select ref={statusRef} className={'form-control'}>
                                                <option value='ACTIVE'>ACTIVE</option>
                                                <option value='INACTIVE'>INACTIVE</option>
                                            </select>
                                        </div>
                                        <div className="w-100 mb-2">
                                            <textarea placeholder={'Description'} className={'form-control'}
                                                      ref={descriptionRef}/>
                                        </div>
                                        <div className='w-100 mb-3 text-end'>
                                            <button type="submit" className="btn btn-primary btn-block mb-4"><i
                                                className='fa fa-paper-plane'/> Sign up
                                            </button>
                                        </div>

                                        {currentErrors.length > 0 ?
                                            <div className="w-100 mt-3">
                                                <div className='alert alert-warning p-2 text-start'>
                                                    {currentErrors.map((error) => <p key={error} className='p-0 m-0'><i
                                                        className='fa fa-exclamation-triangle'/> {error}</p>)}
                                                </div>
                                            </div> : null}


                                        <p>
                                            You already have an account ? <Link to={'/'}>Sign in</Link>
                                        </p>

                                    </form>
                                    :
                                    <div className={'w-100'}>
                                        <div className={'alert alert-success'}>
                                            <p>{successMessage}</p>

                                            <Link to={'/'} className={'btn btn-primary'}><i
                                                className='fa fa-sign-in'/> Sign in</Link>
                                        </div>
                                    </div>


                            }

                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default RegisterPage;
