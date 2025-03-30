# ☕ CyberFlux Coffee Simulator  


No futuro, o **CyberFlux** se tornou um ponto de encontro popular para **gamers, freelancers e estudantes de computação**. No entanto, os recursos são **limitados** (PCs, headsets VR e cadeiras ergonômicas), exigindo um sistema justo para gerenciá-los.  

Este projeto implementa um **simulador de alocação de recursos** baseado em **Threads e Semáforos**, garantindo um uso equilibrado e prevenindo problemas como **deadlock e starvation**.  

---

## ⚙️ Regras e Requisitos  

O **CyberFlux** dispõe dos seguintes recursos:  

- 🖥 **10 PCs de última geração**  
- 🎧 **6 Headsets VR**  
- 💺 **8 Cadeiras ergonômicas**  

Os **clientes chegam em grupos aleatórios** e podem ser de três tipos:  

| Tipo de Cliente  | Prioridade de Recursos |
|------------------|----------------------|
| 🎮 **Gamer**     | PC + Headset VR (depois Cadeira) |
| 💻 **Freelancer**| PC + Cadeira (depois VR) |
| 📚 **Estudante** | Apenas PC |

Cada **cliente é uma thread** que precisa dos recursos para iniciar suas atividades. O sistema **evita deadlock e starvation**, garantindo um uso equilibrado.  

---

## 🚀 Funcionalidades Implementadas  

✔ **Gerenciamento de Threads**: Cada cliente é uma thread separada.  
✔ **Controle de Recursos com Semáforos**: Sincronização eficiente entre clientes.  
✔ **Fila Prioritária**: Clientes mais antigos têm maior prioridade.  
✔ **Prevenção de Deadlock**: Se um cliente não consegue todos os recursos ao mesmo tempo, ele libera o que conseguiu e tenta novamente.  
✔ **Prevenção de Starvation**: O tempo de espera influencia na prioridade dos clientes.  
✔ **Simulação por Tempo Determinado**: O cybercafé opera por **8 horas simuladas**.  
✔ **Geração de Relatório**: No final, um arquivo `relatorio_simulacao.txt` é criado com estatísticas.  

---

## 📊 Estatísticas Geradas  

No final da simulação, o programa exibe e salva no relatório:  

- 📌 **Número total de clientes atendidos**  
- ⏳ **Tempo médio de espera**  
- ❌ **Número de clientes recusados**  
- 📈 **Taxa de utilização dos recursos**  

---

## 🛠 Como Executar  

### 🔹 Pré-requisitos  

- **Java JDK instalado**  
- **Compilador `javac` disponível no terminal**  

### 🔹 Compilação e Execução  

1️⃣ **Acesse a pasta do projeto:**  

```sh
cd CyberFluxCoffee
```

2️⃣ **Compile os arquivos Java:**
```sh
javac -d bin src/main/java/com/cyberfluxcoffee/*.java src/main/java/com/cyberfluxcoffee/domain/*.java
```

3️⃣ **Execute o simulador:**
```sh
java -cp bin com.cyberfluxcoffee.CyberFluxCoffeeSimulator
```

4️⃣ **Veja o relatório final:**

Após a execução, um arquivo relatorio_simulacao.txt será gerado com as estatísticas da simulação.

---

## 🧩 Desafios Técnicos

   Gerenciamento concorrente de múltiplos clientes disputando recursos.

   Sincronização eficiente sem causar deadlock ou starvation.

   Implementação de uma fila de prioridade para garantir justiça na alocação dos recursos.

## 🔍 Testes de Deadlock

Para testar a robustez do sistema, foi reduzida drasticamente a quantidade de PCs disponíveis (de 10 para 3). O número de clientes atendidos caiu, mas não houve deadlock, provando que a solução funciona corretamente.
