package serverSide.serverAL;

import serverSide.ServerCom;
import serverSide.serverGR.GeneralRepository;

import java.net.SocketTimeoutException;
import java.util.*;

import clientSide.stubs.GeneralRepositoryStub;
import commonInfra.*;
import main.*;
/**
 *   
 *   modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento estático dos threads barbeiro.
 *   A comunicação baseia-se em passagem de mensagens sobre sockets usando o protocolo TCP.
 */

public class ArraivalLoungeMain
{
  /**
   *  Número do port de escuta do serviço a ser prestado (4000, por defeito)
   *
   *    @serialField portNumb
   */

   private static final int portNumb = global.SERVER_PORT_NUMB_AL;                         //portNumber

  private static final int List = 0;
   
  public static boolean waitConnection;                              // sinalização de actividade

  /**
   * General Information Repository Stub
   */

  /**
   *  Programa principal.
   */
   public static void main (String [] args)
   {
      ArraivalLounge monitorAL;                            // instanciar o monitor (server)
      ArraivalLoungeInterface aloungeInter;                // interface ao ArraivalLounge
      ServerCom scon, sconi;                               // canais de comunicação
      Proxy cliProxy;                                      // thread agente prestador do serviço
      
     /* estabelecimento do servico */

      scon = new ServerCom (portNumb);                                 // criação do canal de escuta e sua associação
      scon.start ();                                                  // com o endereço público

      
      GeneralRepositoryStub grStub = new GeneralRepositoryStub(global.SERVER_HOST_NAME_GR,global.SERVER_PORT_NUMB_GR);
      monitorAL = new ArraivalLounge(grStub);                                  // activação do serviço
      aloungeInter = new ArraivalLoungeInterface (monitorAL);                 // activação do interface com o serviço
      System.out.println("O serviço foi estabelecido! Arraival Lounge ");
      System.out.println("O servidor esta em escuta na porta: "+ portNumb);

     /* processamento de pedidos */

      waitConnection = true;
      while (waitConnection)
        try
        { sconi = scon.accept ();                          // entrada em processo de escuta
          cliProxy = new Proxy (sconi, aloungeInter);  // lançamento do agente prestador do serviço
          cliProxy.start ();
        }
        catch (SocketTimeoutException e)
        {
        }
      scon.end ();                                         // terminação de operações
      System.out.println("O servidor foi desactivado.");
   }
}
