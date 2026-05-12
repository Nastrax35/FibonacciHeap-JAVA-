package ru.itis.SemWork.FibomacciHeap;

import ru.itis.SemWork.FibomacciHeap.FibonacciHeap;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap();
        Random random = new Random();

        int[] array = new int[10000];
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(2000000);
        }

        List<Long> insertTimes = new ArrayList<>();
        List<Long> insertOps = new ArrayList<>();

        List<Long> searchTimes = new ArrayList<>();
        List<Long> searchOps = new ArrayList<>();

        List<Long> deleteTimes = new ArrayList<>();
        List<Long> deleteOps = new ArrayList<>();

        for (int i = 0; i < array.length; i++) {
            FibonacciHeap.operationCount = 0;
            long start = System.nanoTime();
            heap.insert(array[i]);
            long end = System.nanoTime();

            insertTimes.add(end - start);
            insertOps.add(FibonacciHeap.operationCount);
        }


        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, new Random(42)); // Фиксируем seed для воспроизводимости

        List<Integer> searchValues = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            searchValues.add(array[indices.get(i)]);
        }

        List<Integer> deleteValues = new ArrayList<>();
        for (int i = 100; i < 1100; i++) {
            deleteValues.add(array[indices.get(i)]);
        }

        for (int value : searchValues) {
            FibonacciHeap.operationCount = 0;
            long start = System.nanoTime();
            FibonacciHeap.Node found = heap.find(value);
            long end = System.nanoTime();

            if (found == null) {
                System.err.println("ВНИМАНИЕ: Значение " + value + " не найдено в куче!");
            }

            searchTimes.add(end - start);
            searchOps.add(FibonacciHeap.operationCount);
        }

        for (int value : deleteValues) {
            FibonacciHeap.operationCount = 0;
            long start = System.nanoTime();
            boolean deleted = heap.delete(value);
            long end = System.nanoTime();

            if (!deleted) {
                System.err.println("ВНИМАНИЕ: Значение " + value + " не найдено для удаления!");
            }

            deleteTimes.add(end - start);
            deleteOps.add(FibonacciHeap.operationCount);
        }





        try (PrintWriter writer = new PrintWriter(new FileWriter("insert_results.csv"))) {
            writer.println("LaunchIndex,ExecutionTime_ns,OperationCount");
            for (int i = 0; i < insertTimes.size(); i++) {
                writer.printf(Locale.US, "%d,%d,%d\n", i + 1, insertTimes.get(i), insertOps.get(i));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файлов добавления: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("search_results.csv"))) {
            writer.println("LaunchIndex,ExecutionTime_ns,OperationCount");
            for (int i = 0; i < searchTimes.size(); i++) {
                writer.printf(Locale.US, "%d,%d,%d\n", i + 1, searchTimes.get(i), searchOps.get(i));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файлов поиска: " + e.getMessage());
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("delete_results.csv"))) {
            writer.println("LaunchIndex,ExecutionTime_ns,OperationCount");
            for (int i = 0; i < deleteTimes.size(); i++) {
                writer.printf(Locale.US, "%d,%d,%d\n", i + 1, deleteTimes.get(i), deleteOps.get(i));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файлов удаления: " + e.getMessage());
        }

        double avgInsertTime = calculateAverage(insertTimes);
        double avgInsertOps = calculateAverage(insertOps);

        double avgSearchTime = calculateAverage(searchTimes);
        double avgSearchOps = calculateAverage(searchOps);

        double avgDeleteTime = calculateAverage(deleteTimes);
        double avgDeleteOps = calculateAverage(deleteOps);

        System.out.println("\n=== ИТОГОВЫЕ СРЕДНИЕ МЕТРИКИ ===");
        System.out.printf(Locale.US, "Insert -> Время: %.2f нс | Операций: %.0f\n", avgInsertTime, avgInsertOps);
        System.out.printf(Locale.US, "Find   -> Время: %.2f нс | Операций: %.0f\n", avgSearchTime, avgSearchOps);
        System.out.printf(Locale.US, "Delete -> Время: %.2f нс | Операций: %.0f\n", avgDeleteTime, avgDeleteOps);

    }

    private static double calculateAverage(List<Long> values) {
        long sum = 0;
        for (long val : values) {
            sum += val;
        }
        return (double) sum / values.size();
    }
}