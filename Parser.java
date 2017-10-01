import java.io.File;
import java.util.Scanner;
import java.util.LinkedList;

public class Parser {

	final static int MVUP = 0, MVDN = 1, WRITE = 2, PRINTCHR = 3, PRINTINT = 4;


	public static void main(String[] args){
		if(args.length == 0){
			System.out.println("No Arguments");
			return;
		}
		Scanner input = null;
		try{
			input = new Scanner(new File(args[0]));
		}
		catch(Exception e){
			System.out.println("Invalid Filename");
			return;
		}

		Machine runMachine = new Machine(parse(input));
	}

	private static int[] parse(Scanner input){
		LinkedList<Integer> commands = new LinkedList<>();

		String current;
		while(input.hasNext()){
			current = input.next();
			switch(current){
				case "MVUP":
					commands.add(MVUP);
					break;
				case "MVDN":
					commands.add(MVDN);
					break;
				case "WRITE":
					if(input.hasNext()){
						commands.add(WRITE);
						String arg = input.next();

						// check if it's a character
						if(arg.length() == 3 && arg.charAt(0) == '<' && arg.charAt(2) == '>'){
							commands.add((int)arg.charAt(1));
						}
						else{
							commands.add(Integer.parseInt(arg));
						}
					}
					break;
				case "PRINTCHR":
					commands.add(PRINTCHR);
					break;
				case "PRINTINT":
					commands.add(PRINTINT);
					break;
			}

		}

		return toIntArray(commands);
	} 

	private static int[] toIntArray(LinkedList<Integer> list){
		int[] array = new int[list.size()];
		int index = 0;
		for(int i : list){
			array[index] = i;
			index++;
		}
		return array;
	}
}