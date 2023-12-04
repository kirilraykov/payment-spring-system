import {useContext, useEffect, useRef, useState} from "react";
import Modal from "react-modal";
import axios from "axios";

import UserContext from "../context/user-context";

function CreateTransaction(props) {
    const userContext = useContext(UserContext);
    const [successMessage, setSuccessMessage] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const [transactionTypes, setTransactionTypes] = useState([]);

    const [currentErrors, setCurrentErrors] = useState([]);
    const amountRef = useRef();
    const customerEmailRef = useRef();
    const phoneRef = useRef();
    const transactionTypeRef = useRef();
    const referenceIdRef = useRef();

    function loadTransactionTypes() {

        axios.get(userContext.getBackendUrl() + '/transactions/types')
            .then((response) => {

                setTransactionTypes(response.data);
            }).catch((err) => {
            alert('Error while fetching transaction types');
        });
    }

    function parseAmount(val) {
        if (val.length) {
            val = val.toString().replace(',', '.');
            val = val.toString().replace(' ', '');
            val = parseFloat(val);
            if (isNaN(val)) {
                return "0.00";
            } else return val.toFixed(2);
        } else return "0.00";
    }

    function parseReferenceId(val) {
        if (val.length) {
            if (isNaN(val)) {
                return 0;
            } else return parseInt(val);
        } else return 0;
    }

    function saveTransaction() {
        setCurrentErrors([]);
        var errors = [];


        let amount = amountRef.current.value;
        let customerEmail = customerEmailRef.current.value;
        let phone = phoneRef.current.value;
        let transactionType = transactionTypeRef.current.value;
        let referenceId = referenceIdRef.current.value;

        if (amount.length === 0) errors.push("Empty amount.");
        if (customerEmail.length === 0) errors.push("Empty customer email.");
        if (phone.length === 0) errors.push("Empty phone.");
        if (referenceId.length === 0) errors.push("Empty Reference ID.");
        if (transactionType.length === 0 || transactionType === 'Select type') errors.push("Please, select transaction  type");

        if (errors.length) {
            setCurrentErrors(errors);
        } else {

            let payload = {amount, transactionType, customerEmail, phone, referenceId};

            axios.post(userContext.getBackendUrl() + '/transactions/create/' + props.merchantId, payload)
                .then((response) => {
                    setSuccessMessage('Transactions stored successfully.');
                    setTimeout(function () {
                        setIsModalOpen(false);
                        setSuccessMessage(null);
                        props.reloadTransactions();
                    }, 2000);

                }).catch((err) => {
                let additional_info = null;
                if (err.response.data.message) additional_info = err.response.data.message;
                setCurrentErrors(["Error while saving transaction." + additional_info])
            });
        }
    }

    useEffect(() => {
        loadTransactionTypes();
    }, []);

    return (<div>
        <button type={'button'} className={'btn btn-success btn-sm'} onClick={() => {
            setIsModalOpen(true);
        }}><i className={'fa fa-plus'}/> CREATE TRANSACTION
        </button>
        <Modal isOpen={isModalOpen}
               animationDuration={10000}
               ariaHideApp={false}
               style={{
                   overlay: {backgroundColor: 'rgb(0,0,0,0.7)'}, content: {
                       minWidth: '40vw',
                       top: '30%',
                       boxShadow: '1px 1px 15px black',
                       borderRadius: '10px',
                       backgroundColor: 'white',
                       left: '50%',
                       right: 'auto',
                       bottom: 'auto',
                       marginRight: '-50%',
                       transform: 'translate(-50%, -50%)',
                   }
               }}
        >
            {successMessage !== null ? <div className={'alert alert-success'}>{successMessage}</div> :
                <div className={'w-100'}>
                    <div className={'w-100'}>
                        <h3>CREATE NEW TRANSACTION</h3>
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">Amount</span>
                        <input type="text" className="form-control" name='amount' id='amount' onBlur={(e) => {
                            e.target.value = parseAmount(e.target.value);
                        }} ref={amountRef}/>
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">Reference ID</span>
                        <input type="text" className="form-control" name='referenceId' id='referenceId' onBlur={(e) => {
                            e.target.value = parseReferenceId(e.target.value);
                        }} ref={referenceIdRef}/>
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">Customer Email</span>
                        <input type="email" className="form-control" name='customerEmail' id='customerEmail'
                               ref={customerEmailRef}/>
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">Phone</span>
                        <input type="text" className="form-control" name='phone' id='phone' ref={phoneRef}/>
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">Transaction Type</span>

                        <select name='transactionType' className='form-control' ref={transactionTypeRef}>
                            <option value={null}>Select type</option>
                            {transactionTypes.map((type) => <option value={type} key={type}>{type}</option>)}
                        </select>
                    </div>

                    <div className="w-100 mb-3 text-end">
                        <button type={'button'} onClick={saveTransaction} className={'btn btn-success'}><i
                            className={'fa fa-save'}/> Save transaction
                        </button>
                    </div>

                    {currentErrors.length > 0 ? <div className="w-100 mt-3">
                        <div className='alert alert-warning p-2 text-start'>
                            {currentErrors.map((error) => <p key={error} className='p-0 m-0'><i
                                className='fa fa-exclamation-triangle'/> {error}</p>)}
                        </div>
                    </div> : null}

                </div>

            }

            <button className={'btn btn-light float-end'} onClick={() => {
                setIsModalOpen(false)
            }} type={'button'}><i className={'fa fa-times'}/> Close
            </button>
        </Modal>
    </div>)
}

export default CreateTransaction;
