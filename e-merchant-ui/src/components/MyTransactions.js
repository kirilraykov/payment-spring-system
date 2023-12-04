import {useContext, useEffect, useRef, useState} from "react";
import axios from "axios";
import UserContext from "../context/user-context";
import CreateTransaction from "./CreateTransaction";

function MyTransactions(props){

    const [transactions,setTransactions] = useState([]);
    const userContext =  useContext(UserContext);

    function loadTransactions(){
        axios.get(userContext.getBackendUrl()+"/transactions/"+props.merchantId)
            .then((response)=>{
                setTransactions(response.data);
            }).catch((error)=>{
            alert("Error while fetching transactions...");
        });
    }

    useEffect(()=>{
       loadTransactions();
    },[]);

    return (
        <div>
            <hr/>
            <h3>My transactions</h3>
            <div className={'w-100 mb-2 text-end'}>
                <CreateTransaction reloadTransactions={loadTransactions} merchantId={props.merchantId} />
            </div>
            {transactions.length ?
                <table className={'table table-sm'}><thead><tr><th>ID</th><th>Amount</th><th>Transaction Type</th><th>Status</th><th>Customer email</th><th>Phone</th><th>Reference ID</th></tr></thead><tbody>
                {transactions.map((transaction) =>
                    <tr key={transaction.id}>
                        <td><b style={{opacity:0.6}}>{transaction.id}</b></td>
                        <td>{transaction.amount}</td>
                        <td>{transaction.transactionType}</td>
                        <td>{transaction.status}</td>
                        <td>{transaction.customerEmail}</td>
                        <td>{transaction.phone}</td>
                        <td>{transaction.referenceId}</td>
                    </tr>
                )}
                </tbody></table>:
                <div className={'alert alert-info'}><i className={'fa fa-times'} /> No transactions found </div> }
        </div>
    )
}

export default MyTransactions;
