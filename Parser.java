import java.io.File;
import java.util.Scanner;
import java.util.LinkedList;


public class Parser {

	final static int LIT = -2, REG = -1, MVUP = 0, MVDN = 1, WRITE = 2, PRINTCHR = 3, PRINTINT = 4, LOOP_LIT = 5,
		LOOP_VAR = 6, JUMP_BACK = 7, MVUX = 8, MVDX = 9, PEEK = 10, SET = 11;

	final static int REG_OFFSET = 49;

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

		int[] result = parse(input);
		/*
		for(int i : result){
			System.out.println(i);
		}
		*/
		Machine runMachine = new Machine(result);
		//Machine runMachine = new Machine(parse(input));
	}

	private static int[] parse(Scanner input){
		LinkedList<Integer> commands = new LinkedList<>();

		String current;
		boolean inLoop = false;
		int lengthAtLoopStart = 0;

		while(input.hasNext()){
			current = input.next();
			switch(current){
				case "SET":
					commands.add(SET);

					String arg = input.next();

					if(arg.substring(0,2).equals("RE")){
						//commands.add(REG);
						int regNum = arg.charAt(2) - REG_OFFSET;

						if(!(regNum >= 0 && regNum < 8)){
							syntaxError("Invalid register");
						}
						commands.add(regNum);
					}
					
					determineReference(commands, input);
					break;
				case "MVUP":
					commands.add(MVUP);
					break;
				case "MVDN":
					commands.add(MVDN);
					break;
				case "MVUX":
					commands.add(MVUX);
					if(input.hasNextInt()){
						commands.add(input.nextInt());
					}
					break;
				case "MVDX":
					commands.add(MVDX);
					if(input.hasNextInt()){
						commands.add(input.nextInt());
					}
					break;
				case "WRITE":
					commands.add(WRITE);
					determineReference(commands, input);
					break;
				case "PRINTCHR":
					commands.add(PRINTCHR);
					break;
				case "PRINTINT":
					commands.add(PRINTINT);
					break;
				case "LOOP":
					if(inLoop){
						syntaxError("Nested loops not permitted");
					}

					commands.add(LOOP_LIT);

					// add number of iterations
					if(input.hasNextInt()){
						commands.add(input.nextInt());
					}

					if(!input.next().equals("{")){
						syntaxError("Expected {");
					}

					inLoop = true;
					lengthAtLoopStart = commands.size();
					break;

				case "}":
					if(inLoop){
						commands.add(JUMP_BACK);
						int loopSize = commands.size() - lengthAtLoopStart;
						commands.add(loopSize);
					}
					else{
						syntaxError("Unexpected Character: }");
					}
					break;
				default:
					syntaxError("Unexpected or unrecognized lexeme");
			}

		}

		return toIntArray(commands);
	}

	private static void determineReference(LinkedList<Integer> commands, Scanner input){
		if(input.hasNext()){

			if(input.hasNextInt()){
				commands.add(LIT);
				commands.add(input.nextInt());
				return;
			}

			String arg = input.next();
						
			// check if it's a peek 
			if(arg.equals("PEEK")){
				commands.add(PEEK);
			}
			// check if it's a register
			else if(arg.substring(0,2).equals("RE")){
				commands.add(REG);
				int regNum = arg.charAt(2) - REG_OFFSET;
				if(!(regNum >= 0 && regNum < 8)){
					syntaxError("Invalid register");
				}
				commands.add(regNum);
			}
			// check if it's a character literal
			else if(arg.charAt(0) == '<' && arg.charAt(arg.length() - 1) == '>'){
				commands.add(LIT);
				if(arg.length() == 3){
					commands.add((int)arg.charAt(1));
				}
				else{
					switch(arg.substring(1,arg.length() - 1)){
						case "\\n":
							commands.add((int)('\n'));
							break;
						case "\\sp":
							commands.add((int)(' '));
							break;
						default:
							syntaxError("Unknown special sequence");
					}
				}			
			}
		}
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

	private static void syntaxError(String issue){
		System.out.println("Syntax Error: " + issue);
		System.exit(0);
	}

}