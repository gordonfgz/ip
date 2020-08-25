import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Duke {
    protected String lines = "____________________________________________________________";

    protected String greeting = "____________________________________________________________\n" +
            "Hello! I'm Duke\n" +
            "What can I do for you?\n" +
            "____________________________________________________________";

    protected ArrayList<Task> TaskList = new ArrayList<>();

    private void start() {
        try {
            this.importSavedDataToList();
            Class myClass = getClass();
            URL url = myClass.getResource("Data.txt");
            if (url == null) {
                File file = new File("Data.txt");
                if (file.createNewFile()) {
                    System.out.println(greeting);
                } else {
                    System.out.println(greeting);
                }
            } else {
                System.out.println(greeting);
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private void importSavedDataToList() {
        BufferedReader objReader = null;
        try {
            String strCurrentLine;
            objReader = new BufferedReader(new FileReader("Data.txt"));
            while ((strCurrentLine = objReader.readLine()) != null) {
                String taskIcon = Character.toString(strCurrentLine.charAt(0));
                String doneIcon = Character.toString(strCurrentLine.charAt(4));
                boolean isDone = false;
                if (doneIcon.equals("1")) {
                    isDone = true;
                }
                if (taskIcon.equals("T")) {
                    String description = strCurrentLine.substring(8);
                    Todo task = new Todo(description, isDone);
                    TaskList.add(task);
                } else if (taskIcon.equals("D")) {
                    int indexOfLastDivider = strCurrentLine.lastIndexOf("|");
                    String description = strCurrentLine.substring(8, indexOfLastDivider - 1);
                    String by = strCurrentLine.substring(indexOfLastDivider + 2);
                    Deadline task = new Deadline(description, isDone, by);
                    TaskList.add(task);
                } else if (taskIcon.equals("E")) {
                    int indexOfLastDivider = strCurrentLine.lastIndexOf("|");
                    String description = strCurrentLine.substring(8, indexOfLastDivider - 1);
                    String at = strCurrentLine.substring(indexOfLastDivider + 2);
                    Event task = new Event(description, isDone, at);
                    TaskList.add(task);
                } else {
                    System.out.println("Invalid first Character");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void end() {
        this.saveListToData();
        System.out.println(lines);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(lines);
        System.exit(0);
    }

    private void clearData() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("Data.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writer.print("");
        writer.close();
    }

    private void saveListToData() {
        this.clearData();
        for (Task task : this.TaskList) {
            String input = "";
            if (task instanceof Todo) {
                if (task.isDone) {
                    input = "T | 1 | " + task.description;
                } else {
                    input = "T | 0 | " + task.description;
                }
            } else if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.isDone) {
                    input = "D | 1 | " + deadline.description + " | " + deadline.by;
                } else {
                    input = "D | 0 | " + deadline.description + " | " + deadline.by;
                }
            } else if (task instanceof Event) {
                Event event = (Event) task;
                if (event.isDone) {
                    input = "E | 1 | " + event.description + " | " + event.at;
                } else {
                    input = "E | 0 | " + event.description + " | " + event.at;
                }
            } else {

            }
            try {
                FileWriter writer = new FileWriter("Data.txt", true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(input);
                bufferedWriter.newLine();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void provideList() {
        System.out.println(lines);
        System.out.println("Here are the tasks in your list:");
        for (Task task : this.TaskList) {
            String stringedIndex = Integer.toString(this.TaskList.indexOf(task) + 1);
            String outputLine = stringedIndex + ". " + task;
            System.out.println(outputLine);
        }
        System.out.println(lines);
        this.saveListToData();
    }

    private void markAsDone(String input) {
        String stringIndex = input.substring(5, input.length());
        int index = Integer.parseInt(stringIndex);
        Task chosen = this.TaskList.get(index - 1);
        chosen.finish();
        System.out.println(lines);
        System.out.println("Nice! I've marked this task as done: ");
        System.out.println(chosen);
        System.out.println(lines);
        this.saveListToData();
    }

    private void delete(String input) throws EmptyListException, InvalidListIndexException {
        String stringIndex = input.substring(7, input.length());
        int index = Integer.parseInt(stringIndex);
        if (this.TaskList.isEmpty()) {
            throw new EmptyListException();
        } else if (index > 0 && index <= this.TaskList.size()) {
            Task chosen = this.TaskList.get(index - 1);
            this.TaskList.remove(chosen);
            System.out.println(lines);
            System.out.println(" Noted. I've removed this task: ");
            System.out.println(chosen);
            System.out.println(" Now you have " + Integer.toString(this.TaskList.size()) + " tasks in the list.");
            System.out.println(lines);
        } else {
            throw new InvalidListIndexException();
        }
    }

    private void addTaskToTasklist(Task task) {
        System.out.println(lines);
        System.out.println(" Got it. I've added this task: ");
        this.TaskList.add(task);
        System.out.println("  " + task);
        int listCount = this.TaskList.size();
        System.out.println(" Now you have " + Integer.toString(listCount) + " tasks in the list.");
        System.out.println(lines);
        this.saveListToData();


    }

    private void createAndAddTodo(String input) throws EmptyDescriptionException, WrongFormatException {
        if (input.length() < 5 || input.substring(5).replaceAll("\\s", "").equals("")) {
            throw new EmptyDescriptionException("todo");
        } else if (!Character.toString(input.charAt(4)).equals(" ")) {
            throw new WrongFormatException("todo");
        } else {
            Task task = new Todo(input.substring(5, input.length()));
            this.addTaskToTasklist(task);
        }
    }


    private void createAndAddDeadline(String input) throws EmptyDescriptionException, WrongFormatException {
        if (input.length() < 9 || input.substring(8).replaceAll("\\s", "").equals("")) {
            throw new EmptyDescriptionException("deadline");
        } else if (input.contains("/by")
                && Character.toString(input.charAt(8)).equals(" ")
                && Character.toString(input.charAt(input.indexOf("/") + 3)).equals(" ")
                && Character.toString(input.charAt(input.indexOf("/") - 1)).equals(" ")) {
            String desc = input.substring(9, input.indexOf("/") - 1);
            String by = input.substring(input.indexOf("/") + 4, input.length());
            Task task = new Deadline(desc, by);
            this.addTaskToTasklist(task);
        } else {
            throw new WrongFormatException("deadline");
        }
    }

    private void createAndAddEvent(String input) throws EmptyDescriptionException, WrongFormatException {
        if (input.length() < 6 || input.substring(5).replaceAll("\\s", "").equals("")) {
            throw new EmptyDescriptionException("event");
        } else if (input.contains("/at")
                && Character.toString(input.charAt(5)).equals(" ")
                && Character.toString(input.charAt(input.indexOf("/") + 3)).equals(" ")
                && Character.toString(input.charAt(input.indexOf("/") - 1)).equals(" ")) {
            String desc = input.substring(6, input.indexOf("/") - 1);
            String at = input.substring(input.indexOf("/") + 4, input.length());
            Task task = new Event(desc, at);
            this.addTaskToTasklist(task);
        } else {
            throw new WrongFormatException("event");
        }
    }



    private void newTaskEntry(String input) throws EmptyDescriptionException, WrongFormatException {
        if (input.contains("todo")) {
            this.createAndAddTodo(input);
        } else if (input.contains("deadline")) {
            this.createAndAddDeadline(input);
        } else if (input.contains("event")) {
            this.createAndAddEvent(input);
        }
    }

    private void processInput(String input) throws DukeException {
        if (input.equals("list")) {
            this.provideList();
        } else if (input.contains("done")) {
            this.markAsDone(input);
        } else if (input.contains("todo") || input.contains("deadline") || input.contains("event")) {
            this.newTaskEntry(input);
        } else if (input.equals("bye")) {
            this.end();
        } else if (input.contains("delete")) {
            this.delete(input);
        } else {
            throw new InputNotRecognisedException();
        }
    }

    public static void main(String[] args) throws DukeException {
        Duke duke = new Duke();
        Scanner sc = new Scanner(System.in);
        String input;
        duke.start();
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            try {
                duke.processInput(input);
            } catch (DukeException e) {
                System.out.println(e.toString());
            }

        }
    }
}
