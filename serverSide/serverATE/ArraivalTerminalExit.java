package serverSide.serverATE;

import interfaces.IArraivalTerminalExitPassenger;
import main.global;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import clientSide.*;
import clientSide.stubs.GeneralRepositoryStub;

/**
 * Arraival Terminal shared memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */

public class ArraivalTerminalExit implements IArraivalTerminalExitPassenger{
    /**
    * Arraival Terminal Exit Variable for locking 
	*/
    private final ReentrantLock rl;
    /**
    * Arraival Terminal Exit Conditional variable for waiting all passengers to exit
	*/
    private final Condition waitingEnd;
    /**
    * Arraival Terminal Exit variable to count passengers
	*/
    private int passengers = 0;
    /**
    * Arraival Terminal Exit Conditional variable for nº off all passengers
	*/
    private int nrPassengers;
    /**
     * General Repository
     */
    private GeneralRepositoryStub grStub;
    /**
    * Arraival Terminal Exit boolean to check if all can go home
    */
    private boolean goingHome=true;
    /**
	* Arraival Terminal Exit shared Memory constructor
	* 
	* @param nrPassengers
	* @param rep
	*/
    public ArraivalTerminalExit(GeneralRepositoryStub grStub) {
        rl = new ReentrantLock(true);
        waitingEnd = rl.newCondition();
        this.grStub=grStub;
  
    }

    /**
	 * Puts passenger in {@link  commonInfra.PassengerEXITING_THE_ARRIVAL_TERMINAL} if go home end cycle. 
	 * @param npassengers
	 * @return lastone
	 */
    @Override
    public boolean goHome(int npassengers , int passengerID) {
        rl.lock();
        try {

            //Passenger passenger = (Passenger) Thread.currentThread();
            //rep.passGoHome(passenger.getPassengerID());

            grStub.passGoHome(passengerID);
            System.out.println(npassengers);
            boolean lastPassenger = passengers + npassengers == nrPassengers;
            System.out.println("->" + lastPassenger);
            if(lastPassenger) {
                goingHome = false;
                waitingEnd.signalAll();
            }

            while(goingHome) {
                waitingEnd.await();
            }

            passengers--;
            
            return lastPassenger;

        } catch(Exception ex) {return false;}
        finally {
            rl.unlock();
        }
    }
    /**
	* Wakes upp all passengers and end Cycle Arraival Terminal Exit
	*
	*/
    @Override
    public void awakePassengers(){
        rl.lock();
        try {
            goingHome = false;
            waitingEnd.signalAll();
            
        } catch (Exception e) {}
        finally{
            rl.unlock();
        }
    }

    /**
	*  Returns all passengers in Arraival Terminal Exit
	*  @return passengers
	*/
    @Override
    public int nPassengersDepartureAT(){
        rl.lock();
        int p = this.passengers;
        rl.unlock();
        return p;
    /**
	*  Increments passengers at Arraival Terminal Exit
	*  
    */
    }
    @Override
    public void syncPassenger(){
        rl.lock();
        goingHome = true;
        passengers++;
        rl.unlock();
    }
   
    public void setParameters(int nrPassengers){
        this.nrPassengers = nrPassengers;
    }


}
