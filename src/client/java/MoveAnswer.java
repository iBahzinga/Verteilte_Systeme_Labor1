 public class MoveAnswer{
    public  enum MoveAnswerType {
        NO_EXIST(0),
        INVALID_MOVE(1),
        OPP_GONE(2),
        WIN(3),
        LOSE(4),
        VALID(5);
        private final static MoveAnswerType[] MOVE_ANSWER_TYPES = {NO_EXIST, INVALID_MOVE, OPP_GONE, WIN, LOSE};

        private final static String[] MoveAnswer = {"game_does_not_exist", "invalid_move","opponent_gone", "you_win", "you_lose"};
        public static MoveAnswerType getType(String answer){
            for (int i = 0; i < MoveAnswer.length; i++) {
                if(MoveAnswer[i].equals(answer)){
                    return  MOVE_ANSWER_TYPES[i];
                }
            }
            return VALID;
        }
        private final int type;

        MoveAnswerType(int _t){
            this.type = _t;
        }

        @Override
        public String toString() {
            if(0 <= type && type < MoveAnswer.length){
                return MoveAnswer[type];
            }
            else return "INVALID ANSWER";
        }
    }


    private final MoveAnswerType answer;
    private final Move move;
    MoveAnswer(MoveAnswerType i) {
        this.answer = i;
        move = null;
    }

    MoveAnswer(Move _move){
        this.move = _move;
        answer = MoveAnswerType.VALID;
    }

    MoveAnswer(String strAnswer){
        answer = MoveAnswerType.getType(strAnswer);
        if (answer.equals(MoveAnswerType.VALID)){
            move = new Move(strAnswer);
        }
        else {
            move = null;
        }
    }

     MoveAnswer(String strAnswer, String opp){
         answer = MoveAnswerType.getType(strAnswer);
         if (answer.equals(MoveAnswerType.VALID)){
             move = new Move(strAnswer);
             move.playerID = opp;
         }
         else {
             move = null;
         }
     }

    public boolean is(MoveAnswerType type){
        return answer == type;
    }

     @Override
     public String toString() {
        if (move != null){
            return move.toString();
        }
        return answer.toString();
     }

     public Move getMove() {
         return move;
     }
 }