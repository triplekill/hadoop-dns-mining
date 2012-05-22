/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.covert.dns.parse;

import io.covert.util.UniqueKeyOnlyReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ParseJob extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		ToolRunner.run(new ParseJob(), args);
	}
	
	@Override
	public int run(String[] args) throws Exception {
				
		String inDir = args[0];
		String outDir = args[1];
		
		Configuration conf = getConf();
		
		Job job = new Job(conf);
		job.setJobName(ParseJob.class.getSimpleName()+": inDir="+inDir+", outDir="+outDir);
		job.setJarByClass(getClass());
		
		job.setMapperClass(ParseMapper.class);
		job.setReducerClass(UniqueKeyOnlyReducer.class);
		job.setNumReduceTasks(new JobClient(new JobConf(conf)).getClusterStatus().getTaskTrackers());
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(SequenceFileInputFormat.class);
		FileInputFormat.setInputPaths(job, new Path(inDir));
		
		job.setOutputFormatClass(SequenceFileOutputFormat.class);
		SequenceFileOutputFormat.setOutputPath(job, new Path(outDir));
		SequenceFileOutputFormat.setCompressOutput(job, true);
		job.submit();
		
		return job.waitForCompletion(true)?0:1;
	}

}
