import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

class RandomAI {

    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    Random generator = new Random();

    double t1, t2;
    int me;
    int boardState;
    int state[][] = new int[8][8]; // state[0][0] is the bottom left corner of the board (on the GUI)
    int turn = -1;
    int round;
    
    int validMoves[] = new int[64];
    int numValidMoves;

    static int MAX_DEPTH = 6;
    static int choice = 0;
    static int BOARD_SIZE = 8;

    public int chooseMove(int state[][], int round, boolean myMove, int depth){
        int validMoves[] = new int[64];
        int numValidMoves = getValidMoves(round, state, validMoves, myMove ? me : (me %  2 + 1));

        Map<Integer, Integer> scoreMap = new HashMap<>(); //(move, score)

        if(numValidMoves == 0 || depth == MAX_DEPTH){
            return calculateScore(!myMove, state, round);
        }

        for(int move = 0; move < numValidMoves; move++){
            int row = validMoves[move] / BOARD_SIZE;
            int col = validMoves[move] % BOARD_SIZE;
            state[row][col] = myMove ? me : (me%2 + 1);
            scoreMap.put(move, chooseMove(state, round+1, !myMove, depth+1));
            state[row][col] = 0;
        }

        // if(myMove){
            int bestScore = myMove ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            for(Map.Entry<Integer, Integer> pair : scoreMap.entrySet())
            {
                if((myMove && bestScore < pair.getValue()) || (!myMove && bestScore > pair.getValue())){
                    bestScore = pair.getValue();
                    choice = pair.getKey();
                }
            }
            return bestScore;
        // }
        // else{
        //     //Minimize their score
        //     int bestScore = Integer.MAX_VALUE;
        //     for(Map.Entry<Integer, Integer> pair : scoreMap.entrySet())
        //     {
        //         if(bestScore > pair.getValue()){
        //             bestScore = pair.getValue();
        //             choice = pair.getKey();
        //         }
        //     }
        //     return bestScore;
        // }
    }

    public int calculateScore(boolean myMove, int state[][], int round){
        double percentOpenSpace = 20.0 / round;
        
        // System.out.println("percentOpenSpace: " + percentOpenSpace);
        // int corner_score = 1;
        // if (round <= 20)
        // {
        //     corner_score = 3;
        // }
        int who = myMove ? me : (me % 2 + 1);
        double score = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int positive = state[i][j] == who ? 1 : -1;
                // if (positive == 1) {
                    if (i == 1 || i == 7 || j == 1 || j == 7) {
                        score -= positive * 25 * percentOpenSpace;
                    }
                    else if(j == 0 || j == BOARD_SIZE || i == 0 || i == BOARD_SIZE){
                        score += positive * 25 * percentOpenSpace;

                        if ((i == 0 || i == BOARD_SIZE) && (j == 0 || j == BOARD_SIZE)) {
                            score += positive * 100 * percentOpenSpace;
                        }
                    }
                    score += positive;
                // }
            }
        }
        int actual_score = (int)score;



        if(myMove){
        //    int[] dump = new int[64];
        //    score += getValidMoves(round, state, dump, who);
        }
        else
        {
            actual_score *= -1;
           int[] dump = new int[64];
           score -= getValidMoves(round, state, dump, who);
//
        }

        return actual_score;
    }
    
    // main function that (1) establishes a connection with the server, and then plays whenever it is this player's turn
    public RandomAI(int _me, String host) {
        me = _me;
        initClient(host);

        int myMove;
        
        while (true) {
            System.out.println("Read");
            readMessage();
            
            if (turn == me) {
                System.out.println("Move");
                getValidMoves(round, state);
                
                myMove = move();
                //myMove = generator.nextInt(numValidMoves);        // select a move randomly
                
                String sel = validMoves[myMove] / 8 + "\n" + validMoves[myMove] % 8;
                
                System.out.println("Selection: " + validMoves[myMove] / 8 + ", " + validMoves[myMove] % 8);
                
                sout.println(sel);
            }
        }
        //while (turn == me) {
        //    System.out.println("My turn");
            
            //readMessage();
        //}
    }
    
    // You should modify this function
    // validMoves is a list of valid locations that you could place your "stone" on this turn
    // Note that "state" is a global variable 2D list that shows the state of the game
    private int move() {
        // just move randomly for now
        // int myMove = generator.nextInt(numValidMoves);

        chooseMove(state, round, true, 0);
        
        return choice;
    }
    
    // generates the set of valid moves for the player; returns a list of valid moves (validMoves)
    private void getValidMoves(int round, int state[][]) {
        int i, j;

        numValidMoves = 0;
        if (round < 4) {
            if (state[3][3] == 0) {
                validMoves[numValidMoves] = 3*8 + 3;
                numValidMoves ++;
            }
            if (state[3][4] == 0) {
                validMoves[numValidMoves] = 3*8 + 4;
                numValidMoves ++;
            }
            if (state[4][3] == 0) {
                validMoves[numValidMoves] = 4*8 + 3;
                numValidMoves ++;
            }
            if (state[4][4] == 0) {
                validMoves[numValidMoves] = 4*8 + 4;
                numValidMoves ++;
            }
            System.out.println("Valid Moves:");
            for (i = 0; i < numValidMoves; i++) {
                System.out.println(validMoves[i] / 8 + ", " + validMoves[i] % 8);
            }
        }
        else {
            System.out.println("Valid Moves:");
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (state[i][j] == 0) {
                        if (couldBe(state, i, j, me)) {
                            validMoves[numValidMoves] = i*8 + j;
                            numValidMoves ++;
                            System.out.println(i + ", " + j);
                        }
                    }
                }
            }
        }
        
        
        //if (round > 3) {
        //    System.out.println("checking out");
        //    System.exit(1);
        //}
    }

    // generates the set of valid moves for the player; returns a list of valid moves (validMoves)
    private int getValidMoves(int round, int state[][], int[] validMoves, int me) {
        int i, j;

        int numValidMoves = 0;
        if (round < 4) {
            if (state[3][3] == 0) {
                validMoves[numValidMoves] = 3*8 + 3;
                numValidMoves ++;
            }
            if (state[3][4] == 0) {
                validMoves[numValidMoves] = 3*8 + 4;
                numValidMoves ++;
            }
            if (state[4][3] == 0) {
                validMoves[numValidMoves] = 4*8 + 3;
                numValidMoves ++;
            }
            if (state[4][4] == 0) {
                validMoves[numValidMoves] = 4*8 + 4;
                numValidMoves ++;
            }
            System.out.println("Valid Moves:");
            for (i = 0; i < numValidMoves; i++) {
                System.out.println(validMoves[i] / 8 + ", " + validMoves[i] % 8);
            }
        }
        else {
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    if (state[i][j] == 0) {
                        if (couldBe(state, i, j, me)) {
                            validMoves[numValidMoves] = i*8 + j;
                            numValidMoves ++;
                        }
                    }
                }
            }
        }

        return numValidMoves;
    }


    private boolean checkDirection(int state[][], int row, int col, int incx, int incy, int me) {
        int sequence[] = new int[7];
        int seqLen;
        int i, r, c;
        
        seqLen = 0;
        for (i = 1; i < 8; i++) {
            r = row+incy*i;
            c = col+incx*i;
        
            if ((r < 0) || (r > 7) || (c < 0) || (c > 7))
                break;
        
            sequence[seqLen] = state[r][c];
            seqLen++;
        }
        
        int count = 0;
        for (i = 0; i < seqLen; i++) {
            if (me == 1) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        return true;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        return true;
                    break;
                }
            }
        }
        
        return false;
    }
    
    private boolean couldBe(int state[][], int row, int col, int me) {
        int incx, incy;
        
        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;
            
                if (checkDirection(state, row, col, incx, incy, me))
                    return true;
            }
        }
        
        return false;
    }
    
    public void readMessage() {
        int i, j;
        String status;
        try {
            //System.out.println("Ready to read again");
            turn = Integer.parseInt(sin.readLine());
            
            if (turn == -999) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                
                System.exit(1);
            }
            
            //System.out.println("Turn: " + turn);
            round = Integer.parseInt(sin.readLine());
            t1 = Double.parseDouble(sin.readLine());
            System.out.println(t1);
            t2 = Double.parseDouble(sin.readLine());
            System.out.println(t2);
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    state[i][j] = Integer.parseInt(sin.readLine());
                }
            }
            sin.readLine();
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        
        System.out.println("Turn: " + turn);
        System.out.println("Round: " + round);
        for (i = 7; i >= 0; i--) {
            for (j = 0; j < 8; j++) {
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void initClient(String host) {
        int portNumber = 3333+me;
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            String info = sin.readLine();
            System.out.println(info);
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    
    // compile on your machine: javac *.java
    // call: java RandomGuy [ipaddress] [player_number]
    //   ipaddress is the ipaddress on the computer the server was launched on.  Enter "localhost" if it is on the same computer
    //   player_number is 1 (for the black player) and 2 (for the white player)
    public static void main(String args[]) {
        new RandomAI(Integer.parseInt(args[1]), args[0]);
    }
    
}
