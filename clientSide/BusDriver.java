package clientSide;

import interfaces.*;

import java.util.Random;

import clientSide.stubs.ArrailvalTTransferQuayStub;
import clientSide.stubs.DepartureTerminalTransferQuayStub;
import commonInfra.*;


 /**
*  Bus driver entity 
*  @author João Monteiro 
*  @author Lucas Seabra
*/
public class BusDriver extends Thread{
    /**
     * BusDriver state
     */
    private BusDriverEnum state;
    /**
    * Integer   to count time
    */
    private int time = 0; 
    /**
    * Random delay to use in thread
    */
    private Random rDelay;

     /**
     * Number of passenger riding the bus
     */
    private int passengersRiding = 0;
    /**
    * Arraival Terminal Transfer Quay stub reference
    */
    private ArrailvalTTransferQuayStub attQuayStub;
    /**
     *  Departure Terminal Transfer Quay stub reference
     */
    private DepartureTerminalTransferQuayStub dttQuayStub;
   /**
    *  Bus Capacity
    */
    private final int busSize;
    /**
    * Terminate busDriver cicle if yes
    */
    private boolean end;
    /**
    * Determine busdriver next action
    */
    private BusDriverAction busState;
   
   
    public BusDriver(ArrailvalTTransferQuayStub attQuayStub,DepartureTerminalTransferQuayStub dttQuayStub, int busSize){
        this.state = state.PARKING_AT_THE_ARRIVAL_TERMINAL;
        this.busSize = busSize;
        this.attQuayStub = attQuayStub;
        this.dttQuayStub = dttQuayStub;
        this.end = true;
    }


    @Override
    public void run(){
        while(end){
            switch(state){
                case PARKING_AT_THE_ARRIVAL_TERMINAL: 
                    System.out.println("BusDriver waiting for passengers...");
                    busState = attQuayStub.hasDaysWorkEnded();  
                    if (busState == BusDriverAction.goToDepartureTerminal){
                       
                        passengersRiding = attQuayStub.annoucingBusBoarding();
                        if(passengersRiding >0){
                            System.out.println("Starting jouney to terminal Transfer");
                            state = BusDriverEnum.DRIVING_FORWARD;
                        }
                    }    
                    else if (busState == BusDriverAction.dayEnded){
                        System.out.println("BusDriver's day ended. ");
                        end = false;
                           
                    }
                    break;
                case DRIVING_FORWARD:
                    System.out.println("BusDriver -> DRIVING_FORWARD");
                    state = BusDriverEnum.PARKING_AT_THE_DEPARTURE_TERMINAL;
                    break;
                case PARKING_AT_THE_DEPARTURE_TERMINAL:
                    System.out.println("BusDriver -> PARKING_AT_THE_DEPARTURE_TERMINAL");
                    System.out.println("NUMBER OF PASSENGERS AT THE BUS"+passengersRiding);
                    dttQuayStub.parkTheBusAndLetPassOff(passengersRiding);
                    System.out.println("Passengers left the bus | starting travel back");
                    state = BusDriverEnum.DRIVING_BACKWARD;
                    break;
            
                case DRIVING_BACKWARD:
                    System.out.println("BusDriver -> DRIVING_BACKWARD");
                    state = BusDriverEnum.PARKING_AT_THE_ARRIVAL_TERMINAL;
                    break;

            }

            try {
                Thread.sleep(rDelay.nextInt(10));
                //Thread.sleep(50);
               // time +=250;
            } catch (Exception e) {}
        }
        System.out.println("Bus driver thread Ended");
    }
}



