package c6;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class DelaysWritable implements Writable{
    public IntWritable year=new IntWritable();//年
    public IntWritable month = new IntWritable();//月份
    public IntWritable date = new IntWritable();//日期
    public IntWritable dayOfWeek = new IntWritable();//星期中的一天
    public IntWritable arrDelay = new IntWritable();//到达延迟
    public IntWritable depDelay = new IntWritable();//起飞延迟
    public Text originAirportCode = new Text();//起飞机场代码
    public Text destAirportCode = new Text();//目的地机场代码
    public Text carrierCode = new Text();//运载物代码

    public DelaysWritable(){
    }


    public void setDelaysWritable(DelaysWritable dw){
        this.year = dw.year;
        this.month = dw.month;
        this.date = dw.date;
        this.dayOfWeek = dw.dayOfWeek;
        this.arrDelay = dw.arrDelay;
        this.depDelay = dw.depDelay;
        this.originAirportCode = dw.originAirportCode;
        this.destAirportCode = dw.destAirportCode;
        this.carrierCode = dw.carrierCode;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.year.write(out);
        this.month.write(out);
        this.date.write(out);
        this.dayOfWeek.write(out);
        this.arrDelay.write(out);
        this.depDelay.write(out);
        this.originAirportCode.write(out);
        this.destAirportCode.write(out);
        this.carrierCode.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.year.readFields(in);
        this.month.readFields(in);
        this.date.readFields(in);
        this.dayOfWeek.readFields(in);
        this.arrDelay.readFields(in);
        this.depDelay.readFields(in);
        this.originAirportCode.readFields(in);
        this.destAirportCode.readFields(in);
        this.carrierCode.readFields(in);
    }

}
