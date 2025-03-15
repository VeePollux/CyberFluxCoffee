package domain;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PolicyManager {
    private final Objects pcs;
    private final Objects vr;
    private final Objects cadeiras;
    private final FinalStatistics finalStatistics;
    private final Semaphore lock = new Semaphore(1, true);

    private final PriorityQueue<Client> filaClientes = new PriorityQueue<>(
            Comparator.comparingLong(Client::getChegada)  // Prioriza primeiro pela chegada
                    .thenComparingInt(Client::getTipo)  // Em caso de empate, usa o tipo sendo da maior prioridade Gamer > Freelancer > Estudante
    );

    public PolicyManager(Objects pcs, Objects vr, Objects cadeiras, FinalStatistics finalStatistics) {
        this.pcs = pcs;
        this.vr = vr;
        this.cadeiras = cadeiras;
        this.finalStatistics = finalStatistics;
    }
    // Usa o lock para garantir que a alocação de recursos não seja interrompida
    public boolean tentarAlocarRecursos(Client cliente) throws InterruptedException {
        lock.acquire();
        try {
            return alocarRecursos(cliente);
        } finally {
            lock.release();}
    }

    private boolean alocarRecursos(Client cliente) {
        if (cliente.getTipo() == 0) { // Gamer
            if (pcs.tentarAlocar()) {
                if (vr.tentarAlocar()) {
                    cliente.setUsouPC(true);
                    cliente.setUsouVR(true);
                    return true;
                } else {
                    pcs.liberar(); // Libera o PC caso não consiga alocar VR
                    return false;
                }
            }
        } else if (cliente.getTipo() == 1) { // Freelancer
            if (pcs.tentarAlocar()) {
                if (cadeiras.tentarAlocar()) {
                    cliente.setUsouPC(true);
                    cliente.setUsouCadeira(true);
                    return true;
                } else {
                    pcs.liberar(); // Libera o PC caso não consiga alocar VR
                    return false;
                }
            }
        } else if (cliente.getTipo() == 2) { // Estudante
            if (pcs.tentarAlocar()) {
                cliente.setUsouPC(true);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean tentarAlocarOpcional(Client cliente) throws InterruptedException {
            if (cliente.getTipo() == 0) { // Gamer tenta cadeira
                if (cadeiras.tentarAlocar()) {
                    cliente.setUsouCadeira(true);
                    return true;
                }
            } else if (cliente.getTipo() == 1) { // Freelancer tenta VR
                if (vr.tentarAlocar()) {
                    cliente.setUsouVR(true);
                    return true;}
            }
        return false;
    }

    public void liberarRecursos(Client cliente) {
        try {
            //System.out.println("🔑 Tentando adquirir lock para liberar objetos...");
            if (!lock.tryAcquire(10, TimeUnit.SECONDS)) {
                //System.err.println("🚨 Falha ao adquirir lock em liberarObjects! Cliente: " + cliente.getIdClient());
                return;
            }
            System.out.println("Cliente " + cliente.getIdClient() + " liberando objetos...");
            if (cliente.usouPC()) pcs.liberar();
            if (cliente.usouVR()) vr.liberar();
            if (cliente.usouCadeira()) cadeiras.liberar();

            // Resetamos as flags para o cliente
            cliente.setUsouPC(false);
            cliente.setUsouVR(false);
            cliente.setUsouCadeira(false);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.release();
        }
    notificarClientes();
    }

    private void notificarClientes() {
        if (filaClientes.isEmpty()) return;
        //System.out.println("📢 Tentando notificar clientes... Clientes na fila: " + filaClientes.size());
        try {
            if (!lock.tryAcquire(10, TimeUnit.SECONDS)) {
                //System.err.println("🚨 Timeout ao tentar notificar clientes!");
                return;
            }

            Client clienteAtendido = null;

            for (Client cliente : new ArrayList<>(filaClientes)) {
                if (podeAlocarRecursos(cliente)) {
                    clienteAtendido = cliente;
                    break;}
            }
            if (clienteAtendido != null) {
                filaClientes.remove(clienteAtendido);
                System.out.println("🔔 Notificando cliente: " + clienteAtendido.getIdClient());
                clienteAtendido.notificarCliente();}

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.release();}
    }

    private boolean podeAlocarRecursos(Client cliente) {
        if (cliente.getTipo() == 0) { // Gamer
            return pcs.temDisponivel() && vr.temDisponivel();
        } else if (cliente.getTipo() == 1) { // Freelancer
            return pcs.temDisponivel() && cadeiras.temDisponivel();
        } else if (cliente.getTipo() == 2) { // Estudante
            return pcs.temDisponivel();}
        return false;
    }

    public void addFila(Client cliente) {
        try {
            lock.acquire(); // Espera até adquirir o lock
            if (!filaClientes.contains(cliente)) {
                filaClientes.add(cliente);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.release();}
    }

    public void removerDaFila(Client cliente) {
        try {
            lock.acquire();
            filaClientes.remove(cliente);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.release();}
    }
}
