
package serverSide.serverATTQ;

import java.io.*;
import java.net.SocketTimeoutException;


import serverSide.ServerCom;
import shared_regions.GeneralRepository;

public class ArraivalTerminalTransferQuayMain {
    /**
   *  Número do port de escuta do serviço a ser prestado (4000, por defeito)
   *
   *    @serialField portNumb
   */

   private static final int portNumb = 3002;
   public static boolean waitConnection;                              // sinalização de actividade

  /**
   * Programa principal.
   * 
   * @throws IOException
   */

  public static void main(String[] args) throws IOException
   {  
    
        File logger = new File("logger.txt");
        if(logger.createNewFile()){
            //System.out.println("Logger created: " + logger.getName());
        }
        else{
            logger.delete();
            logger.createNewFile();
            // System.out.println("File already exists.");
        }

        GeneralRepository genInfoRepo = new GeneralRepository(logger);

        ArraivalTerminalTransferQuay monitorATTQ;                         // barbearia (representa o serviço a ser prestado)
        ArraivalTerminalTransferQuayInterface attQuayInter;               // interface à barbearia
        ServerCom scon, sconi;                                          // canais de comunicação
        Proxy cliProxy;                                                // thread agente prestador do serviço

        /* estabelecimento do servico */

        scon = new ServerCom (portNumb);                     // criação do canal de escuta e sua associação
        scon.start ();                                       // com o endereço público
        monitorATTQ = new ArraivalTerminalTransferQuay(genInfoRepo);                   // activação do serviço
        attQuayInter = new ArraivalTerminalTransferQuayInterface (monitorATTQ);        // activação do interface com o serviço
        System.out.println("O serviço foi estabelecido!");
        System.out.println("O servidor esta em escuta.");

        /* processamento de pedidos */
        //NAO SEI ONDE METER ISTO MAS É NECESSARIO!!!
        genInfoRepo.close();
        waitConnection = true;
        while (waitConnection)
            try
            { sconi = scon.accept ();                          // entrada em processo de escuta
            cliProxy = new Proxy (sconi, attQuayInter);  // lançamento do agente prestador do serviço
            cliProxy.start ();
            }
            catch (SocketTimeoutException e)
            {
            }
        scon.end ();                                         // terminação de operações
        System.out.println("O servidor foi desactivado.");
    }    
}