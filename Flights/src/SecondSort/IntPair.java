package SecondSort;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public class IntPair implements WritableComparable<IntPair> {
    private int first;
    private int second;

    public IntPair(){}

    public IntPair(int first,int second){
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public int compareTo(IntPair o){
        if (first != o.first){
            return first < o.first ? -1 : 1;
        }
        /*else if (second != o.second){ //代替自定义的SortComparator
            return second < o.second ? -1 : 1;
        }*/
        else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (o instanceof IntPair){
            IntPair r = (IntPair) o;
            return r.first == first && r.second == second;
        }else {
            return false;
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(first);
        out.writeInt(second);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        first = in.readInt();
        second = in.readInt();
    }

    @Override
    public int hashCode() {
        //return first * 157 + second; //将所有的保存在一个输出里
        return first/10-3;
    }
}
