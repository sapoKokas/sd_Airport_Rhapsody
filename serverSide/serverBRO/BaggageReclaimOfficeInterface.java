package serverSide.serverBRO;


import commonInfra.*;
public class BaggageReclaimOfficeInterface {
    /**
   *  Barbearia (representa o serviço a ser prestado)
   *
   *    @serialField bShop
   */

   private BaggageReclaimOffice monitorBRO;
    /**
     * Instanciação do interface ao Arraival Lounge.
     * @param monitorATE
     */

   public BaggageReclaimOfficeInterface(BaggageReclaimOffice monitorBRO){
       this.monitorBRO = monitorBRO;
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
   public BROMessage processAndReply(BROMessage inMessage) throws BROMessageException
  {
    BROMessage outMessage = null;                           // mensagem de resposta

    /* validação da mensagem recebida */

     switch (inMessage.getMsgType()) 
     {  
        case BROMessage.COMPLAIN:
            if(inMessage.getBaggageList() ==null || inMessage.getBaggageList().size() ==0)
                throw new BROMessageException("Set de IDS das malas do utilizador inválido ou vazio", inMessage);
            break;
        case BROMessage.SHUTDOWN:
            break;
        default:
             throw new BROMessageException("Tipo inválido!", inMessage);
     }

     /* seu processamento */

     switch (inMessage.getMsgType())
     {
        case BROMessage.COMPLAIN:
                    monitorBRO.complain(inMessage.getBaggageList() , inMessage.getPassengerID());
                    outMessage = new BROMessage(BROMessage.ACK);
                    break;   
        case BROMessage.SHUTDOWN:
                    BaggageReclaimOfficeMain.waitConnection = false;
                    (((Proxy) (Thread.currentThread())).getScon()).setTimeout(10);  
                    outMessage = new BROMessage(BROMessage.ACK);
                    break;                                             
    }
    return (outMessage);
   }
}