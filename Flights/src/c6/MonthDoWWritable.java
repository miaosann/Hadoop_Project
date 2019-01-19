package c6;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

public class MonthDoWWritable implements WritableComparable<MonthDoWWritable>{
    public int monthSort = 1;
    public int dowSort = -1;

    public IntWritable month=new IntWritable();
    public IntWritable dayOfWeek = new IntWritable();

    /**
     * Hadoop框架调用无参构造函数进行实例化，
     * 这个无参构造函数必须得加
     */
    public MonthDoWWritable(){
    }

    /**
     * 该方法是一个序列化方法，将字段值写到输出流中，
     * 注意，只有在需要的时候才会被序列化输出
     * @param out
     * @throws IOException
     */
    @Override
    public void write(DataOutput out) throws IOException {
        this.month.write(out);
        this.dayOfWeek.write(out);
    }

    /**
     * 该方法负责到Writable实例的反序列化流，
     * 注意字段的顺序和write方法中要是相同的
     * @param in
     * @throws IOException
     */
    @Override
    public void readFields(DataInput in) throws IOException {
        this.month.readFields(in);
        this.dayOfWeek.readFields(in);
    }

    /**
     * 用于比较两个Writable实例，compareTo方法中保证了
     * 输出的数据是按照月份升序并且星期降序排列的
     * @param second
     * @return
     */
    @Override
    public int compareTo(MonthDoWWritable second) {
        if(this.month.get()==second.month.get()){
            return -1*this.dayOfWeek.compareTo(second.dayOfWeek);
        }
        else{
            return 1*this.month.compareTo(second.month);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MonthDoWWritable)) {
            return false;
        }
        MonthDoWWritable other = (MonthDoWWritable)o;
        return this.month.get() == other.month.get() && this.dayOfWeek.get() == other.dayOfWeek.get();
    }

    @Override
    public int hashCode() {
        return (this.month.get()-1);
    }
}
