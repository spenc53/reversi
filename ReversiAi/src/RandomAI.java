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

    static boolean found_corner = false;


//    static int R1 = 4;
//    static int R2 = -4;
//    static int R3 = 6;
//    static int R4 = -8;
//    static int R6 = -24;
//    static int R5 = 99;
//
//    int lookupScores[][] = {
//            {R5, R4, R3, R3, R3, R3, R4, R5},
//            {R4, R6, R2, R2, R2, R2, R6, R4},
//            {R3, R2, R1, R1, R1, R1, R2, R3},
//            {R3, R2, R1, R1, R1, R1, R2, R3},
//            {R3, R2, R1, R1, R1, R1, R2, R3},
//            {R3, R2, R1, R1, R1, R1, R2, R3},
//            {R4, R6, R2, R2, R2, R2, R6, R4},
//            {R5, R4, R3, R3, R3, R3, R4, R5}
//    };

    // static int R1 = 1000;
    // static int R2 = -18;
    // static int R3 = 8;
    // static int R4 = 6;
    // static int R5 = -24;
    // static int R6 = 4;
    // static int R7 = -3;
    // static int R8 = 7;
    // static int R9 = 4;
    // static int R10 = 0;

    // // Brian's values.
    // static int R1 = 75;
    // static int R2 = -1;
    // static int R3 = 8;
    // static int R4 = 8;
    // static int R5 = -8;
    // static int R6 = 0;
    // static int R7 = 0;
    // static int R8 = 0;
    // static int R9 = 0;
    // static int R10 = 0;

    // Vaishnavi's values.
    static int R1 = 99;
    static int R2 = -8;
    static int R3 = 8;
    static int R4 = 6;
    static int R5 = -24;
    static int R6 = -4;
    static int R7 = -3;
    static int R8 = 7;
    static int R9 = 4;
    static int R10 = 0;

    int lookupScores[][] = {
        {R1, R2, R3, R4, R4, R3, R2, R1},
        {R2, R5, R6, R7, R7, R6, R5, R2},
        {R3, R6, R8, R9, R9, R8, R6, R3},
        {R4, R7, R9, R10, R10, R9, R7, R4},
        {R4, R7, R9, R10, R10, R9, R7, R4},
        {R3, R6, R8, R9, R9, R8, R6, R3},
        {R2, R5, R6, R7, R7, R6, R5, R2},
        {R1, R2, R3, R4, R4, R3, R2, R1}
    };
    
    int validMoves[] = new int[64];
    int numValidMoves;

    static int MAX_DEPTH = 8;
    static int choice = 0;
    static int BOARD_SIZE = 8;

    public int minimax(int state[][], int round, boolean myMove, int depth, int alpha, int beta) { // player may be "computer" or "opponent"

        int c = 0;
        int validMoves[] = new int[64];
        int numValidMoves = getValidMoves(round, state, validMoves, myMove ? me : (me %  2 + 1));
        if(depth == MAX_DEPTH || numValidMoves == 0){
            return calculateScore(myMove, state, round);
        }
//        if(){
//            return minimax(state, round+1, !myMove, depth+1, alpha, beta);
//        }

        if (myMove) {
            for(int move = 0; move < numValidMoves; move++){
                int row = validMoves[move] / BOARD_SIZE;
                int col = validMoves[move] % BOARD_SIZE;
                state[row][col] = myMove ? me : (me%2 + 1);

                int [][] newState = new int[state.length][];
                for(int i = 0; i < state.length; i++)
                    newState[i] = state[i].clone();

                changeColors(newState, row, col, myMove ? me : (me%2 + 1));

                int score = minimax(newState, round + 1, !myMove, depth + 1, alpha, beta);
                state[row][col] = 0;
                if (score > alpha){
                    alpha = score;
                    c = move;
                }
                if (alpha >= beta) break;
            }
            choice = c;
            return alpha;
        }
        else {
            for(int move = 0; move < numValidMoves; move++){
                int row = validMoves[move] / BOARD_SIZE;
                int col = validMoves[move] % BOARD_SIZE;
                state[row][col] = myMove ? me : (me%2 + 1);

                int [][] newState = new int[state.length][];
                for(int i = 0; i < state.length; i++)
                    newState[i] = state[i].clone();

                changeColors(newState, row, col, myMove ? me : (me%2 + 1));
//                printBoard(newState);

                int score = minimax(newState, round + 1, !myMove, depth + 1, alpha, beta);
                state[row][col] = 0;
                if (score < beta){
                    choice = move;
                    beta = score;
                }
                if (alpha >= beta) break;
            }
            choice = c;
            return beta;
        }
    }

    public void printBoard(int state[][]){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }


    public static void checkDirectionColor(int state[][],int row, int col, int incx, int incy, int turn) {
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
            if (turn == 0) {
                if (sequence[i] == 2)
                    count ++;
                else {
                    if ((sequence[i] == 1) && (count > 0))
                        count = 20;
                    break;
                }
            }
            else {
                if (sequence[i] == 1)
                    count ++;
                else {
                    if ((sequence[i] == 2) && (count > 0))
                        count = 20;
                    break;
                }
            }
        }

        if (count > 10) {
            if (turn == 0) {
                i = 1;
                r = row+incy*i;
                c = col+incx*i;
                while (state[r][c] == 2) {
                    state[r][c] = 1;
                    i++;
                    r = row+incy*i;
                    c = col+incx*i;
                }
            }
            else {
                i = 1;
                r = row+incy*i;
                c = col+incx*i;
                while (state[r][c] == 1) {
                    state[r][c] = 2;
                    i++;
                    r = row+incy*i;
                    c = col+incx*i;
                }
            }
        }
    }

    public static void changeColors(int state[][] ,int row, int col, int turn) {
        int incx, incy;

        for (incx = -1; incx < 2; incx++) {
            for (incy = -1; incy < 2; incy++) {
                if ((incx == 0) && (incy == 0))
                    continue;

                checkDirectionColor(state, row, col, incx, incy, turn);
            }
        }
    }

    // public int chooseMove(int state[][], int round, boolean myMove, int depth){
    //     int validMoves[] = new int[64];
    //     int numValidMoves = getValidMoves(round, state, validMoves, myMove ? me : (me %  2 + 1));

    //     Map<Integer, Integer> scoreMap = new HashMap<>(); //(move, score)
    //     if (round >= 20) {
    //         MAX_DEPTH = 6;
    //     }

    //     if(numValidMoves == 0 || depth == MAX_DEPTH){
    //         return calculateScore(!myMove, state, round);
    //     }

    //     for(int move = 0; move < numValidMoves; move++){
    //         int row = validMoves[move] / BOARD_SIZE;
    //         int col = validMoves[move] % BOARD_SIZE;
    //         state[row][col] = myMove ? me : (me%2 + 1);
    //         int score = chooseMove(state, round+1, !myMove, depth+1);
    //         scoreMap.put(move, score);
    //         // if(myMove && score > defaultScore) break;
    //         // if(!myMove && score < defaultScore) break;
    //         state[row][col] = 0;
    //     }

    //     // if(myMove){
    //         int bestScore = myMove ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    //         for(Map.Entry<Integer, Integer> pair : scoreMap.entrySet())
    //         {
    //             if((myMove && bestScore < pair.getValue()) || (!myMove && bestScore > pair.getValue())){
    //                 bestScore = pair.getValue();
    //                 choice = pair.getKey();
    //             }
    //         }
    //         return bestScore;
    //     // }
    //     // else{
    //     //     //Minimize their score
    //     //     int bestScore = Integer.MAX_VALUE;
    //     //     for(Map.Entry<Integer, Integer> pair : scoreMap.entrySet())
    //     //     {
    //     //         if(bestScore > pair.getValue()){
    //     //             bestScore = pair.getValue();
    //     //             choice = pair.getKey();
    //     //         }
    //     //     }
    //     //     return bestScore;
    //     // }
    // }

    public static double root(double num, double root)
    {
        return Math.pow(Math.E, Math.log(num)/root);
    }

    public void adjustCorners(int round) {
        int r1 = R1 - R1 * round / (BOARD_SIZE * BOARD_SIZE) + 1;
        int r2 = R2 - R2 * round / (BOARD_SIZE * BOARD_SIZE) + 1;
        int r3 = R3 - R3 * round / (BOARD_SIZE * BOARD_SIZE) + 1;
        int r4 = R4 - R4 * round / (BOARD_SIZE * BOARD_SIZE) + 1;

        lookupScores = new int[][]{
            {r1, r2, r3, r4, r4, r3, r2, r1},
            {r2, R5, R6, R7, R7, R6, R5, r2},
            {r3, R6, R8, R9, R9, R8, R6, r3},
            {r4, R7, R9, R10, R10, R9, R7, r4},
            {r4, R7, R9, R10, R10, R9, R7, r4},
            {r3, R6, R8, R9, R9, R8, R6, r3},
            {r2, R5, R6, R7, R7, R6, R5, r2},
            {r1, r2, r3, r4, r4, r3, r2, r1}
        };
    }

    public int calculateScore(boolean myMove, int state[][], int round){
        int us = myMove ? me : (me % 2 + 1);
        int them = myMove ? (me % 2 + 1) : me;

    //     int [][] newState = new int[lookupScores.length][];
    //     for(int i = 0; i < lookupScores.length; i++)
    //         newState[i] = lookupScores[i].clone();

    //    if(this.state[0][0] == us){
    //        newState[0][1] = 8;
    //        newState[1][0] = 8;
    //        newState[1][1] = 8;
    //    }
    //    if(this.state[0][7] == us){
    //        newState[1][7] = 8;
    //        newState[0][6] = 8;
    //        newState[1][6] = 8;
    //    }
    //    if(this.state[7][0] == us){
    //        newState[7][1] = 8;
    //        newState[6][0] = 8;
    //        newState[6][1] = 8;
    //    }
    //    if(this.state[7][7] == us){
    //        newState[7][6] = 8;
    //        newState[6][7] = 8;
    //        newState[6][6] = 8;
    //    }

        int actual_score = 0;
//        int[] dump = new int[64];
//        if(round <= 30){
//            actual_score -= getValidMoves(round, state, dump, them);
//        }
//        if (round <= 20){
//            actual_score += getValidMoves(round, state, dump, us);
//        }

        // adjustCorners(round);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                    // If risky territory.
                if (state[i][j] == us) {
                    // actual_score += newState[i][j] - newState[i][j] * round / (BOARD_SIZE * BOARD_SIZE) + 1;
                    actual_score += lookupScores[i][j];
                }
//                else if(state[i][j] == them){
//                    actual_score -= newState[i][j];
//                }
            }
        }

        if(myMove){
//             actual_score *= -1;
        }
        else
        {
             actual_score *= -1;
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

        minimax(state, round, true, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        
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
