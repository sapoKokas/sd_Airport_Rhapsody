package serverSide.serverAL;

import commonInfra.*;
import clientSide.*;
import clientSide.stubs.GeneralRepositoryStub;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

import interfaces.*;
import main.global;

/**
 * Arraival Lounge shared memory region.
 * 
 * @author Lucas Seabra
 * @author Joao Monteiro
 */

public class ArraivalLounge implements IArraivalLoungePassenger , IArraivalLoungePorter{
	/**
    * Arraival Lounge Variable for locking 
	*/
	private final ReentrantLock rl;

	/**
    * Arraival Lounge Memory for all baggages
    */
	private ArrayList<Baggage> memBag;
	/**
    * Arraival Lounge Conditional variable for waiting for porter
    */
	private final Condition cPorter;

	/**
    * Arraival Lounge Conditional variable for waiting for plane
    */
	private final Condition waitForPlane;
	/**
    * Arraival Lounge variable to count Passengers
    */
	private int nPassengers=0;
	/**
    * Arraival Lounge determine maxPassengers
    */
	private int maxPassengers;
	/**
    * Arraival Lounge boolean for end of cycle
    */
	private boolean dayEnded=false;
	/**
	 * Arraival Lounge boolean to collect bag
	 * 
	 */
	private boolean collect = false;
	/**
	*  Boolean for porter if should continue to collect bags
	*/
	private boolean porterAvailable=false;
	/**
	* List of Bags for each flight.
	*/ 
	private List<List<Baggage>> bagsPerFlight;
	/**
     * The general repository of information.
     */
	private GeneralRepositoryStub grStub;

	/** 
	* Arraival Lounge shared Memory constructor
    * @param repository General repository of information
    * @param bagsPerFlight List of bags for each flight
    */
	public ArraivalLounge(GeneralRepositoryStub grStub) {
		//this.maxPassengers = maxPassengers;
		
		rl = new ReentrantLock(true);
		cPorter = rl.newCondition();
        waitForPlane = rl.newCondition();
        this.grStub=grStub;
		
		
	}
	
    public void setParameters(int maxPassengers, List<List<Baggage>> bagsPerFlight) {
        if (maxPassengers > 0)
            this.maxPassengers = maxPassengers;
        if (bagsPerFlight.size() > 0)
            this.bagsPerFlight = bagsPerFlight;
        System.out.println("Arrival lounge carregado com " + maxPassengers + " passageiros e malas:" + bagsPerFlight.toString());
        reportInitialStatus();
    }

	/**
	 * Returns passenger action in {@link commonInfra.PassengerAction} state. <p/>
	 * Disembarks passenger and notifies Porter
	 * @param goHome Passenger situation - True for going home, false otherwise
	 * 
	 */
	@Override
    public void whatShouldIDO(Boolean goHome,int bags , int passengerID) {
        rl.lock();
        try {
			//Passenger passenger = (Passenger) Thread.currentThread();
			//int bags = passenger.getFlightBags();
			//System.out.println("BOMDIA !!!!!!!!!!!!!!!!!!!!!!");
			if(goHome){
                //System.out.println("BOMDIA !!!!!!!!!!!!!!!!!!!!!!");    
				grStub.addFinalDestinations();
			}
			
            else {
                System.out.println("add transit");
				grStub.addTransit();
			}
            
        
            while(!porterAvailable){
                cPorter.await();
			}
            nPassengers++;
        
             
			if(nPassengers == 1){
                System.out.println("start next flight -> "+bagsPerFlight.get(0).size());
				grStub.startNextFlight(bagsPerFlight.get(0).size());
			} 
            
            System.out.println("passenger init");
            grStub.passengerInit(PassengerEnum.AT_THE_DISEMBARKING_ZONE, bags, goHome ? "FDT" : "TRF",passengerID );
			
            
            if(nPassengers == maxPassengers) {
                collect = true;
                nPassengers = 0;
                waitForPlane.signal();
            }

            
        } catch(Exception ex) {    
        } finally {
            rl.unlock();
        }
    }

	/**
	 * Porter in {@link commonInfra.PorterEnum.WAITING_FOR_A_PLANE_TO_LAND} state
	 * @return dayEnded
	 * 
	 */	
	@Override
    public boolean takeARest() {
       
        rl.lock();
        try {
            
            porterAvailable = true;
            cPorter.signalAll();

            grStub.porterWaitingLanding();
            while(!collect && !dayEnded) {
				//System.out.println("BOMDIA");
				waitForPlane.await();
				
            }
            //System.out.println("Arraival Lounge saiu do  Take a rest");
            memBag = new ArrayList<>();
            if(!dayEnded){
                List<Baggage> flightBags = bagsPerFlight.remove(0);
                for(int b = 0; b < flightBags.size(); b++) {
                    memBag.add(flightBags.get(b));
                    grStub.addBag();
                   
                }
            }
            
            porterAvailable = false;
            collect = false;
            System.out.println("Arraival Lounge Take a rest:"+!dayEnded);
            return !dayEnded;

        } catch(Exception ex) {  
            return true;  
        } finally {
            rl.unlock();
        }
    }
	/**
	 * Porter in {@link  commonInfra.PorterEnum.AT_THE_PLANES_HOLDWAITING_FOR_PLANE_TO_LAND} state
	 * @return baggage
	 * 
	 */	
	@Override
	public Baggage tryToCollectABag(){
		
		rl.lock();
		try{
			if(memBag.size() > 0) {
				Baggage tempbagg = memBag.remove(0);
				grStub.porterCollectBag();
				//System.out.println(memBag.size());
				return tempbagg;
			}
			else 
				return null;
		}
		catch(Exception ex){
			return null;
		}
		finally{
		rl.unlock();
		}
        
	}
	
	@Override 
    public void endOfDay() {
        rl.lock();
        try {
            dayEnded = true;
            waitForPlane.signal();
        } catch(Exception ex) {}
        finally {
            rl.unlock();
        }
    }
	

	  /**
     * Escrever o estado inicial (operação interna).
     * <p>
     * Os barbeiros estão a dormir e os clientes a realizar as tarefas do dia a dia.
     */

    private void reportInitialStatus() {
        // TextFile log = new TextFile (); // instanciação de uma variável de tipo
        // ficheiro de texto

        // if (!log.openForWriting (".", fileName))
        // { GenericIO.writelnString ("A operação de criação do ficheiro " + fileName +
        // " falhou!");
        // System.exit (1);
        // }
        // log.writelnString (" Problema dos barbeiros sonolentos");
        // log.writelnString ("\nNúmero de iterações = " + nIter + "\n");
        // if (!log.close ())
        // { GenericIO.writelnString ("A operação de fecho do ficheiro " + fileName + "
        // falhou!");
        // System.exit (1);
        // }
        // reportStatus ();
    }

    /**
     * Escrever o estado actual (operação interna).
     * <p>
     * Uma linha de texto com o estado de actividade dos barbeiros e dos clientes é
     * escrito no ficheiro.
     */

    private void reportStatus() {
        // TextFile log = new TextFile (); // instanciação de uma variável de tipo
        // ficheiro de texto
        // String lineStatus = ""; // linha a imprimir

        // if (!log.openForAppending (".", fileName))
        // { GenericIO.writelnString ("A operação de criação do ficheiro " + fileName +
        // " falhou!");
        // System.exit (1);
        // }
        // for (int i = 0; i < nBarber; i++)
        // switch (stateBarber[i])
        // { case SLEEPING: lineStatus += " DORMINDO ";
        // break;
        // case WORKING: lineStatus += " ACTIVIDA ";
        // break;
        // }
        // for (int i = 0; i < nCustomer; i++)
        // switch (stateCustomer[i])
        // { case LIVNORML: lineStatus += " VIVVNRML ";
        // break;
        // case WANTCUTH: lineStatus += " QUERCORT ";
        // break;
        // case WAITTURN: lineStatus += " ESPERAVZ ";
        // break;
        // case CUTHAIR: lineStatus += " CORTACBL ";
        // break;
        // }
        // log.writelnString (lineStatus);
        // if (!log.close ())
        // { GenericIO.writelnString ("A operação de fecho do ficheiro " + fileName + "
        // falhou!");
        // System.exit (1);
        // }
    }


}
	
