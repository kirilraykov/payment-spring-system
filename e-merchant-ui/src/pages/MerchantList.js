import {useContext, useEffect, useState} from "react";
import axios from "axios";
import UserContext from "../context/user-context";
import Modal from 'react-modal';
import ImportMerchants from "../components/ImportMerchants";

function MerchantList() {
    const userContext = useContext(UserContext);
    const [merchants, setMerchants] = useState([]);
    const [transactions, setTransactions] = useState([]);

    const [isModalOpen, setIsModalOpen] = useState(false);

    function loadMerchants() {
        axios.get(userContext.getBackendUrl() + '/merchants')
            .then((response) => {
                setMerchants(response.data);
            }).catch((error) => {

        });
    }

    function loadTransactions(merchantId) {
        axios.get(userContext.getBackendUrl() + "/transactions/" + merchantId)
            .then((response) => {
                setTransactions(response.data);
            }).catch((error) => {
            alert("Error while fetching transactions...");
        });
    }

    function previewTransactions(merchantId) {
        setTransactions([]);
        loadTransactions(merchantId);
        setIsModalOpen(true);
    }

    useEffect(() => {
        if (userContext.user.userType.toLowerCase() === 'admin')
            loadMerchants();
    }, []);


    if (userContext.user.userType.toLowerCase() !== 'admin') return "";
    return (
        <div>
            <div className={'row'}>
                <div className={'col-lg-12 text-end'}>
                    <ImportMerchants/>
                </div>
            </div>

            <p>Merchants <span className={'badge bg-dark text-light'}>{merchants.length}</span></p>
            <table className='table table-hover table-sm'>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Email</th>
                    <th>Username</th>
                    <th>Total Transactions Sum</th>
                    <th>Status</th>
                    <th>Transactions</th>
                </tr>
                </thead>
                <tbody>
                {merchants.map((merchant) =>
                    <tr key={merchant.id}>
                        <td style={{opacity: 0.3}}>{merchant.id}</td>
                        <td><b>{merchant.name}</b></td>
                        <td><b>{merchant.description}</b></td>
                        <td>{merchant.email}</td>
                        <td>{merchant.username}</td>
                        <td>{merchant.totalTransactionSum}</td>
                        <td>{merchant.status}</td>
                        <td>
                            <button className={'btn btn-sm btn-light'} type={'button'} onClick={(e) => {
                                previewTransactions(merchant.id)
                            }}>Preview transactions
                            </button>
                        </td>
                    </tr>
                )}
                </tbody>
            </table>

            <Modal isOpen={isModalOpen}
                   animationDuration={10000}
                   ariaHideApp={false}
                   style={{
                       overlay:
                           {
                               backgroundColor: 'rgb(0,0,0,0.7)',
                           },
                       content: {
                           minWidth: '60vw',
                           top: '40%',
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

                {transactions.length ?

                    <table className={'table table-sm'}>
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Amount</th>
                            <th>Transaction Type</th>
                            <th>Status</th>
                            <th>Customer email</th>
                            <th>Phone</th>
                            <th>Reference ID</th>
                        </tr>
                        </thead>
                        <tbody>

                        {transactions.map((transaction) =>
                            <tr key={transaction.id}>
                                <td><b style={{opacity: 0.6}}>{transaction.id}</b></td>
                                <td>{transaction.amount}</td>
                                <td>{transaction.transactionType}</td>
                                <td>{transaction.status}</td>
                                <td>{transaction.customerEmail}</td>
                                <td>{transaction.phone}</td>
                                <td>{transaction.referenceId}</td>
                            </tr>
                        )}
                        </tbody>
                    </table> :
                    <div className={'alert alert-info'}><i className={'fa fa-times'}/> No transactions found </div>}

                <button className={'btn btn-light float-end'} onClick={() => {
                    setIsModalOpen(false)
                }} type={'button'}><i className={'fa fa-times'}/> Close
                </button>
            </Modal>

        </div>
    );
}

export default MerchantList;
