package serverSide.serverBCP;


import commonInfra.*;
public class BaggageCollectionPointInterface {
    /**
   *  Barbearia (representa o serviço a ser prestado)
   *
   *    @serialField bShop
   */

   private BaggageCollectionPoint monitorBCP;
    /**
     * Instanciação do interface ao Arraival Lounge.
     * @param monitorATE
     */

   public BaggageCollectionPointInterface(BaggageCollectionPoint monitorBCP){
       this.monitorBCP = monitorBCP;
   }

   /**
    * Processamento das mensagens através da execução da tarefa correspondente.
    * Geração de uma mensagem de resposta.
    *
    * @param inMessage mensagem com o pedido
    *
    * @return mensagem de resposta
    * @throws BCPMessageException
    *
    * @throws MessageException    se a mensagem com o pedido for considerada
    *                             inválida
    */

   public BCPMessage processAndReply(BCPMessage inMessage) throws BCPMessageException
  {
    BCPMessage outMessage = null;                           // mensagem de resposta

    /* validação da mensagem recebida */

     switch (inMessage.getMsgType()) 
     {
        case BCPMessage.REQ_CARRY_IT_TO_APPROPRIATE_STORE:
            break;
        case BCPMessage.REQ_GO_COLLECT_A_BAG:
            break;
        case BCPMessage.REQ_NO_MORE_BAGS_TO_COLLECT:
            break;
        case BCPMessage.REQ_RESET_STATE:
            break;
        default:
             throw new BCPMessageException("Tipo inválido!", inMessage);
     }

     /* seu processamento */

     switch (inMessage.getMsgType())
     {
        case BCPMessage.REQ_CARRY_IT_TO_APPROPRIATE_STORE:
                    monitorBCP.carryItToAppropriateStore(inMessage.getBaggage());
                    outMessage = new BCPMessage(BCPMessage.CARRY_IT_TO_APPROPRIATE_STORE_DONE);
                    break;                    
        case BCPMessage.REQ_GO_COLLECT_A_BAG:
                    Baggage bag = monitorBCP.goCollectABag(inMessage.getBaggageList());
                    outMessage = new BCPMessage(BCPMessage.GO_COLLECT_A_BAG_DONE , bag);
                    break;                               
        case BCPMessage.REQ_NO_MORE_BAGS_TO_COLLECT:
                    monitorBCP.noMoreBagsToCollect();
                    outMessage = new BCPMessage(BCPMessage.NO_MORE_BAGS_TO_COLLECT_DONE);
                    break;                              
        case BCPMessage.REQ_RESET_STATE:
                    monitorBCP.resetState();
                    outMessage = new BCPMessage(BCPMessage.RESET_STATE_DONE);                         
                    break;                             
    }
     
    return (outMessage);
   }
}