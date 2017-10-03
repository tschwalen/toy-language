public class Machine {
	
	final static int INITIAL_TAPE_SIZE = 500;

	final static int MVUP = 0, MVDN = 1, WRITE = 2, PRINTCHR = 3, PRINTINT = 4, LOOP_LIT = 5,
		LOOP_PEEK = 6, JUMP_BACK = 7, MVUX = 8, MVDX = 9;

	int tapeLength;
	int[] tape;
	int headPosition;

	public static void main(String[] args){
		int[] testPrgm = {
			WRITE,
			45,
			MVUP,
			WRITE,
			63,
			PRINTCHR,
			MVDN,
			PRINTCHR
		};
		Machine testMachine = new Machine(testPrgm);
	}

	public Machine(int[] cmds){
		tapeLength = INITIAL_TAPE_SIZE;
		tape = new int[INITIAL_TAPE_SIZE];
		headPosition = 0;
		execute(cmds);
	}

	private void execute(int[] cmds){
		// replace with a stack
		int loopCtrl = 0;
		int jumpLength = 0;
		for(int pc = 0; pc < cmds.length; pc++){

			switch(cmds[pc]){
				case MVUP:
					moveUp();
					break;
				case MVDN:
					moveDown();
					break;
				case MVUX:
					pc++;
					headPosition += cmds[pc];
					checkTape();
					break;
				case MVDX:
					pc++;
					headPosition -= cmds[pc];
					checkTape();				
					break;
				case WRITE:
					pc++;
					write(cmds[pc]);
					break;
				case PRINTINT:
					System.out.print(readInt());
					break;
				case PRINTCHR:
					System.out.print(readChar());
					break;
				case LOOP_LIT:						// LOOP <number of iterations>
					pc++;
					loopCtrl = cmds[pc];
					break;
				case JUMP_BACK:        				// JUMP_BACK <# of lexemes>
					if(loopCtrl > 1){
						loopCtrl--;
						pc -= cmds[pc + 1]; // jump back value
					}
					else{
						pc += 2; // skip the jump back number
					}
					break;


			}
		}
	}

	public int readInt(){
		return tape[headPosition];
	}

	public char readChar(){
		return (char)tape[headPosition];
	}

	public void moveUp(){
		headPosition++;
		checkTape();
	}

	public void moveDown(){
		headPosition--;
		checkTape();
	}

	public void write(int value){
		tape[headPosition] = value;
	}

	private void checkTape(){
		if(headPosition < 0){
			resize(false);
		}
		else if(headPosition > tapeLength){
			resize(true);
		}
	}

	public void resize(boolean up){
		
		int[] newTape = new int[tapeLength + INITIAL_TAPE_SIZE];
		tapeLength = newTape.length;
		int offset = up? 0 : INITIAL_TAPE_SIZE;

		for(int i = 0; i < tape.length; i++){
			newTape[offset + i] = tape[i];
		}

		headPosition += offset;

	}

}