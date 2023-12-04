import SideBar from "./SideBar";
import UserNavigation from "./UserNavigation";

function Layout(props) {

    return (
        <div>
            <div className="container-fluid">
                <div className="row flex-nowrap">
                    <div className="col-auto col-md-2 col-xl-2 px-sm-2 px-0 bg-dark hidden-mobile "
                         id={'sideBarContainer'}>
                        <SideBar/>
                    </div>
                    <div className="col py-3" style={{paddingLeft: '0px'}}>
                        <div className='w-100' style={{minHeight: '30px'}}>
                            <div className='bg-dark text-light'
                                 style={{position: 'fixed', width: '84vw', top: '0px', zIndex: 200}}>
                                <div className='row' style={{height: '50px', paddingTop: '12px'}}>
                                    <div className='col-lg-12 col-sm-12 ps-4'>
                                        <UserNavigation className='float-start'/>
                                    </div>
                                </div>
                            </div>
                        </div>


                        <div className='w-100 pt-3 ps-2'>
                            {props.children}
                        </div>

                    </div>
                </div>
            </div>
        </div>
    );
}

export default Layout;
