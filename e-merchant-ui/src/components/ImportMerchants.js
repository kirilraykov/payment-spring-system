import {useContext, useState} from "react";
import Modal from "react-modal";
import axios from "axios";
import UserContext from "../context/user-context";
import {useCookies} from "react-cookie";

function ImportMerchants() {
    const [accessToken, setAccessToken] = useCookies(['bearer_token'])

    const userContext = useContext(UserContext);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    function handleUpload(e) {
        e.preventDefault();
        const form = document.querySelector("#uploadForm");

        if (document.querySelector('#csvFile').value) {
            const formData = new FormData(form);

            for (const value of formData.values()) {
                console.log(value);
            }

            axios
                .post(userContext.getBackendUrl() + "/users/import", formData, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                        "Access-Control-Allow-Origin": "*",
                        "Authorization": 'Bearer ' + accessToken.bearer_token,
                    },
                })
                .then((res) => {
                    console.log(res);
                    setSuccess('The file has been imported successfully.')
                })
                .catch((err) => {
                    console.log(err);
                    var errorInfo = null;
                    if (err.response.data.message) errorInfo = err.response.data.message;
                    setError('Error while uploading file.' + errorInfo);
                });


        } else setError('Please select CSV file');
    }

    return (<div>
        <button className={'btn btn-primary'} onClick={(e) => {
            setIsModalOpen(true)
        }}><i className="fa fa-file-import"/> Import Merchants With CSV File
        </button>

        <Modal isOpen={isModalOpen}
               animationDuration={10000}
               ariaHideApp={false}
               style={{
                   overlay: {backgroundColor: 'rgb(0,0,0,0.7)'},
                   content: {
                       minWidth: '20vw',
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

            <div className={'w-100'}>

                <form id='uploadForm' onSubmit={handleUpload}>
                    <input type='file' name='csv-file' accept={'.csv'} id='csvFile' required/> <br/>
                    <button className={'btn btn-primary mt-2'}><i className={'fa fa-upload'}/> Upload file</button>

                </form>
                <br/>
            </div>
            <div className={'w-100 mt-2'}>
                {error !== null ? <div className={'alert alert-warning'}>{error}</div> : ""}
            </div>
            <div className={'w-100 mt-2'}>
                {success !== null ? <div className={'alert alert-success'}>{success}</div> : ""}
            </div>
            <button className={'btn btn-light float-end'} onClick={() => {
                setIsModalOpen(false)
            }} type={'button'}><i className={'fa fa-times'}/> Close
            </button>
        </Modal>

    </div>)

}

export default ImportMerchants;
