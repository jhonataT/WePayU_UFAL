package br.ufal.ic.p2.wepayu.utils;

import java.io.*;

public class TxtFileManager {
    private String fileName;
    private FileWriter fileWriter;

    public TxtFileManager(String fileName) throws IOException {
        this.fileName = "src/br/ufal/ic/p2/wepayu/payroll/"+fileName;

        try {
            this.fileWriter = new FileWriter(this.fileName);
        }  catch(IOException e) {
            throw new IOException("Erro ao criar arquivo.");
        }
    }
    public void addingContent(String fileTitle) throws IOException {
         String caminhoArquivo = "caminho/do/seu/arquivo.txt";

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            // Escrever o título do arquivo
            bufferedWriter.write(fileTitle);
            bufferedWriter.newLine();
            bufferedWriter.write("====================================");
            bufferedWriter.newLine();
            bufferedWriter.newLine(); // Adicionar linhas em branco

            // Escrever o conteúdo formatado
            writeFormattedContent(bufferedWriter);

            System.out.println("Conteúdo adicionado com sucesso.");

        } catch (IOException e) {
            throw new IOException("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

    private static void writeFormattedContent(BufferedWriter writer) throws IOException {
        // Aqui você pode adicionar as informações formatadas conforme necessário
        // Exemplo:
        writer.write("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo");
        writer.newLine();
        writer.write("==================================== ===== ===== ============= ========= =============== ======================================");
        writer.newLine();
        // Adicionar mais linhas conforme necessário
        // ...

        // Adicionar linha em branco
        writer.newLine();
        // Adicionar mais informações
        // ...
    }

    public String getContent() throws FileNotFoundException {
        String row;

        try {
            FileReader fileReader = new FileReader(this.fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((row = bufferedReader.readLine()) != null) {
                System.out.println(row);
            }

            bufferedReader.close();
        } catch(FileNotFoundException e) {
            throw new FileNotFoundException("Erro ao ler o arquivo");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo");
        }

        return row;
    }

    public static String getContent(String fileName) throws FileNotFoundException {
        String row;

        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((row = bufferedReader.readLine()) != null) {
                System.out.println(row);
            }

            bufferedReader.close();
        } catch(FileNotFoundException e) {
            throw new FileNotFoundException("Erro ao ler o arquivo");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo");
        }

        return row;
    }
}
