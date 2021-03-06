package serverSide.serverDTE;


import commonInfra.*;
public class DepartureTerminalEntranceInterface {
    /**
   *  Barbearia (representa o serviço a ser prestado)
   *
   *    @serialField bShop
   */
   private DepartureTerminalEntrance monitorDTE;
    /**
     * Instanciação do interface ao Arraival Lounge.
     * @param monitorATE
     */

   public DepartureTerminalEntranceInterface(DepartureTerminalEntrance monitorDTE){
       this.monitorDTE = monitorDTE;
   }
   /**
    * Processamento das mensagens através da execução da tarefa correspondente.
    * Geração de uma mensagem de resposta.
    *
    * @param inMessage mensagem com o pedido
    *
    * @return mensagem de resposta
    * @throws BROMessageException
    *
    * @throws MessageException    se a mensagem com o pedido for considerada
    *                             inválida
    */
   public DTEMessage processAndReply(DTEMessage inMessage) throws DTEMessageException
  {
    DTEMessage outMessage = null;                           // mensagem de resposta

    /* validação da mensagem recebida */

     switch (inMessage.getMsgType()) 
     {
        case DTEMessage.SYNC_PASSENGER:
            break;
        case DTEMessage.AWAKE_PASSENGERS:
            break;
        case DTEMessage.N_PASSENGERS_DEPARTURE_TENTRANCE:
            break;
        case DTEMessage.PREPARE_NEXT_LEG:
            break;
        case DTEMessage.SET_PARAM:
            break;
        case DTEMessage.SHUTDOWN:
            break;
        default:
             throw new DTEMessageException("Tipo inválido!", inMessage);
     }
     /* seu processamento */
     switch (inMessage.getMsgType())
     {
        case DTEMessage.SYNC_PASSENGER:
                    monitorDTE.syncPassenger();
                    outMessage = new DTEMessage(DTEMessage.ACK);
                    break;
        case DTEMessage.AWAKE_PASSENGERS:
                    monitorDTE.awakePassengers();
                    outMessage = new DTEMessage(DTEMessage.ACK);
                    break;
        case DTEMessage.N_PASSENGERS_DEPARTURE_TENTRANCE:
                    int nPassenger = monitorDTE.nPassengersDepartureTEntrance();                                           
                    outMessage = new DTEMessage(DTEMessage.ACK , nPassenger);
                    break;
        case DTEMessage.SET_PARAM:
                    monitorDTE.setParemeters(inMessage.nPassenger());
                    outMessage = new DTEMessage(DTEMessage.ACK);
                    break;
        case DTEMessage.PREPARE_NEXT_LEG:
                    boolean lastPassenger = monitorDTE.prepareNextLeg(inMessage.nPassenger(), inMessage.getPassengerID());
                    outMessage = new DTEMessage(DTEMessage.ACK, lastPassenger);
                    break;
        case DTEMessage.SHUTDOWN:
                    DepartureTerminalEntranceMain.waitConnection = false;
                    (((Proxy) (Thread.currentThread())).getScon()).setTimeout(10);
                    outMessage = new DTEMessage(DTEMessage.ACK);
                    break;
    }
    return (outMessage);
   }
}