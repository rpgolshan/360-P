import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.SortedMapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TextAnalyzer extends Configured implements Tool {

  public static class TextMapper extends Mapper<LongWritable, Text, Text, MapWritable>{

    private Text word = new Text();
    private Text temp_word = new Text();

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
	
          String s = value.toString();
          s = s.toLowerCase();
          s = s.replaceAll("[^a-zA-Z0-9]"," ").replaceFirst("^ +", "");
          String sp[] = s.split("\\s+");
          Set<String> set = new HashSet<String>();
          for (int i = 0; i < sp.length; i++) {
              if (set.contains(sp[i])) continue;
              set.add(sp[i]);
              word.set(sp[i]); //context word
              MapWritable m = new MapWritable();
              for (int j = 0; j < sp.length; j++) {
                if (j == i) continue;
                temp_word.set(sp[j]);
                IntWritable temp_value = (IntWritable) m.get(temp_word);
                temp_value = (temp_value == null ? new IntWritable(1):new IntWritable(temp_value.get() + 1));
                m.put(new Text(sp[j]), temp_value);
              }

             context.write(word, m);
        }
  }
}

  public static class TextReducer extends Reducer<Text,MapWritable,Text, Text> {

    private Text word = new Text();
    private Text word2 = new Text();
    private final static Text emptyText = new Text("");
    public void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
        SortedMapWritable temp_map = new SortedMapWritable();
       for (MapWritable map: values) {
            
           for (Writable query: map.keySet()) {
               Text t = (Text) query;
                IntWritable temp_value = (IntWritable) temp_map.get(t);
                IntWritable temp2 = (IntWritable) map.get(t);
                temp_value = (temp_value == null ? new IntWritable(temp2.get()):new IntWritable(temp_value.get() + temp2.get()));
                temp_map.put(t, temp_value);
           } 
       }
       
       context.write(key, emptyText);
       for (Writable query: temp_map.keySet()) {
          String s, s2;
          Text t = (Text) query;
          s = "<" + t.toString() + ",";
          s2 = temp_map.get(query).toString() + ">";
          word.set(s);
          word2.set(s2);
          context.write(word, word2);

       }
       context.write(emptyText, emptyText);

    }
  }

public int run(String[] args) throws Exception {
    Configuration conf = this.getConf();

    // Create job
    Job job = new Job(conf, "rpg499_jtf698"); // Replace with your EIDs
    job.setJarByClass(TextAnalyzer.class);

    // Setup MapReduce job
    job.setMapperClass(TextMapper.class);
    //   Uncomment the following line if you want to use Combiner class
    // job.setCombinerClass(TextCombiner.class);
    job.setReducerClass(TextReducer.class);

    // Specify key / value types (Don't change them for the purpose of this assignment)
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    //   If your mapper and combiner's  output types are different from Text.class,
    //   then uncomment the following lines to specify the data types.
    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(MapWritable.class);
    
    // Input
    FileInputFormat.addInputPath(job, new Path(args[0]));
    job.setInputFormatClass(TextInputFormat.class);

    // Output
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    job.setOutputFormatClass(TextOutputFormat.class);

    // Execute job and return status
    return job.waitForCompletion(true) ? 0 : 1;
}

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
    System.exit(res);
  }
}
