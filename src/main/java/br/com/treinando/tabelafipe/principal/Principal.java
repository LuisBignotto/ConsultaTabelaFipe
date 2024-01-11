package br.com.treinando.tabelafipe.principal;

import br.com.treinando.tabelafipe.model.Dados;
import br.com.treinando.tabelafipe.model.Modelos;
import br.com.treinando.tabelafipe.model.Veiculo;
import br.com.treinando.tabelafipe.service.ConsumoApi;
import br.com.treinando.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {
        String menu = """
            *** OPÇÕES ***
            Carro
            Moto
            Caminhão
            
            Digite uma das opções para consulta:
            
            """;

        System.out.println(menu);

        String opcao = scanner.nextLine();

        String endereco = "";

        String json = "";

        switch (opcao.toLowerCase()){
            case "carro":
                endereco = URL_BASE + "carros/marcas";
                json = consumo.obterDados(endereco);
                break;
            case "moto":
                endereco = URL_BASE + "motos/marcas";
                json = consumo.obterDados(endereco);
                break;
            case "caminhão":
                endereco = URL_BASE + "caminhoes/marcas";
                json = consumo.obterDados(endereco);
                break;
            default:
                System.out.println("Opção Inválida, digite novamente.");
                exibeMenu();
                break;
        }

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta: ");
        String opcaoCodigo = scanner.nextLine();

        endereco = endereco + "/" + opcaoCodigo + "/modelos";

        json = consumo.obterDados(endereco);

        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");

        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite o nome do veiculo a ser buscado: ");
        String nomeVeiculo = scanner.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação: ");
        String codigoModelo = scanner.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";

        json = consumo.obterDados(endereco);

        List<Dados> veiculosLista = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        String novoEndereco= "";

        for(int i = 0; i < veiculosLista.size(); i++){
            novoEndereco = endereco + "/" + veiculosLista.get(i).codigo();
            json = consumo.obterDados(novoEndereco);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);

        System.out.println("\nDeseja buscar outro veiculo?");
        String resposta = scanner.nextLine();

        if(resposta.equals("sim")) exibeMenu();
    }
}
