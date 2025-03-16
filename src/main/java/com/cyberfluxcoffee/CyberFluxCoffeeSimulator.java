import domain.Client;
import domain.FinalStatistics;
import domain.Objects;
import domain.PolicyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Se estiver usando o windows e queira ver os logs, tire os emojis do código, pois o windows não suporta emojis

public class CyberFluxCoffeeSimulator {
    public static void main(String[] args) {

        int tempoSimulacao = 5 * 60 * 1000; // 5 minuto de simulação real
        System.out.println("Aguarde o tempo simulacao de " + tempoSimulacao/60000 + " minutos para ver o resultado final.\nCaso queira ver os logs além do relatório final, há partes do código com mensagens estratégicas que estão comentados. \n");
        Objects pcs = new Objects(10);
        Objects vr = new Objects(6);
        Objects cadeiras = new Objects(8);

        FinalStatistics finalStatistics = new FinalStatistics();
        PolicyManager policyManager = new PolicyManager(pcs, vr, cadeiras, finalStatistics);

        Random random = new Random();
        long inicio = System.currentTimeMillis();
        List<Client> clientesAtivos = new ArrayList<>();

        while (System.currentTimeMillis() - inicio < tempoSimulacao) {
            Client cliente = new Client(random.nextInt(3), policyManager, finalStatistics);
            //Comentário para o tipo de clinte que entrou
            //System.out.println("👤 Cliente " + cliente.getIdClient() + " chegou!" + (cliente.getTipo() == 0 ? " (Gamer)" : cliente.getTipo() == 1 ? " (Freelancer)" : " (Estudante)"));
            cliente.start();
            clientesAtivos.add(cliente);

            try {
                Thread.sleep(random.nextInt(7_000)); // Chegada aleatória de clientes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //System.out.println("Objetos disponíveis: " + pcs.getDisponiveis() + " PCs, " + vr.getDisponiveis() + " VRs, " + cadeiras.getDisponiveis() + " cadeiras");
        }

        System.out.println("\n⚠️  Tempo de simulação encerrado! Removendo clientes ativos...");

        Client.encerrarSimulacao();

// Depois, verifique quais clientes ainda estão vivos e marque como recusados
        for (Client cliente : clientesAtivos) {
            if (cliente.isAlive()) {
                //Comentario para o tipo de cliente que foi morto ao final da simulação
                //System.out.println("🚨 Cliente " + cliente.getIdClient() + " foi morto" +
                 //       (cliente.getTipo() == 0 ? " (Gamer)" : cliente.getTipo() == 1 ? " (Freelancer)" : " (Estudante)"));
                cliente.interrupt();

                // Se o cliente ainda estava ativo quando o café fechou, ele foi recusado
                finalStatistics.registrarRecusa();}
        }

        for (Client cliente : clientesAtivos) {
            try {
                cliente.join(); // Aguarda a thread terminar antes de continuar para não continuar executando enquanto o relatorio é gerado
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();}
        }
// Gera o relatório final após encerrar tudo
        finalStatistics.gerarRelatorio();}
}
