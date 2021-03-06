
package serverSide.serverTSA;


import commonInfra.*;
public class TemporaryStorageAreaInterface {
    /**
   *  Barbearia (representa o serviço a ser prestado)
   *
   *    @serialField bShop
   */

   private TemporaryStorageArea monitorTSA;
    /**
     * Instanciação do interface ao Arraival Lounge.
     * @param monitorTSA
     */

   public TemporaryStorageAreaInterface(TemporaryStorageArea monitorTSA){
       this.monitorTSA = monitorTSA;
   }

   /**
    * Processamento das mensagens através da execução da tarefa correspondente.
    * Geração de uma mensagem de resposta.
    *
    * @param inMessage mensagem com o pedido
    *
    * @return mensagem de resposta
    * @throws TSAMessageException
    *
    * @throws MessageException    se a mensagem com o pedido for considerada
    *                             inválida
    */

   public TSAMessage processAndReply(TSAMessage inMessage) throws TSAMessageException
  {
    TSAMessage outMessage = null;                           // mensagem de resposta

    /* validação da mensagem recebida */

     switch (inMessage.getMsgType()) 

     {  case TSAMessage.SHUTDOWN:
            break;
        case TSAMessage.CARRY_IT_TO_APPROPRIATE_STORE:
            if(inMessage.getBaggage()==null)
                throw new TSAMessageException("No bag", inMessage);
            break;
        default:
             throw new TSAMessageException("Tipo inválido!", inMessage);
     }

     /* seu processamento */

     switch (inMessage.getMsgType())
     {
        case TSAMessage.CARRY_IT_TO_APPROPRIATE_STORE:
                    monitorTSA.carryItToAppropriateStore(inMessage.getBaggage());
                    outMessage = new TSAMessage(TSAMessage.ACK);
                    break;
        case TSAMessage.SHUTDOWN:
                    TemporaryStorageAreaMain.waitConnection = false;
                    (((Proxy) (Thread.currentThread())).getScon()).setTimeout(10);
                    outMessage = new TSAMessage(TSAMessage.ACK);
                    break;
    }
    return (outMessage);
   }
}