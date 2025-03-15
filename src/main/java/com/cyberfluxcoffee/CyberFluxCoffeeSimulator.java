import domain.Client;
import domain.FinalStatistics;
import domain.Objects;
import domain.PolicyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CyberFluxCoffeeSimulator {
    public static void main(String[] args) {
        int tempoSimulacao = 5 * 60 * 1000; // 5 minuto de simulação real

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
            System.out.println("👤 Cliente " + cliente.getIdClient() + " chegou!" + (cliente.getTipo() == 0 ? " (Gamer)" : cliente.getTipo() == 1 ? " (Freelancer)" : " (Estudante)"));
            cliente.start();
            clientesAtivos.add(cliente);

            try {
                Thread.sleep(2_000 + random.nextInt(3_000)); // Chegada aleatória de clientes a cada 9 a 19 segundos reais
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Objetos disponíveis: " + pcs.getDisponiveis() + " PCs, " + vr.getDisponiveis() + " VRs, " + cadeiras.getDisponiveis() + " cadeiras");
        }

        System.out.println("\n⚠️  Tempo de simulação encerrado! Removendo clientes ativos...");

        Client.encerrarSimulacao();
        //System.out.println(clientesAtivos.size() + " clientes ativos");


// Depois, verifique quais clientes ainda estão vivos e marque como recusados
        for (Client cliente : clientesAtivos) {
            if (cliente.isAlive()) {
                System.out.println("🚨 Cliente " + cliente.getIdClient() + " foi morto" +
                        (cliente.getTipo() == 0 ? " (Gamer)" : cliente.getTipo() == 1 ? " (Freelancer)" : " (Estudante)"));
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
