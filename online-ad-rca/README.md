# Overview
This is the actual implementation of the AD and RCA analysis system in Flink. 
Here you will find the Scala code implementation along with the sbt configuration 
used to build the .jar executables.

# Configuration
The `src/main/resources/application.conf` file serves as the configuration file 
of the Flink Jobs. It consists of various sections that each one of them 
- kafka:
  - bootstrap-servers
- input_stream
  - input_topic: The name of the Kafka topic to read input stream of events from
  - timestamp_field: The field name for the JSON input records of the field that will represent timestamp
  - value_field: The field name for the JSON input records of the field that will represent the metric
  - dimensions: Definition of the various dimensions that will be considered by the RCA component
    - names: List of dimension names as appear in the JSON records
    - definitions: For each dimension provide its type (value_type), parent dimension (parent_dimension) and group it belongs to (better see example of for this definition `src/main/resources/application.conf`)
- anomaly_detection:
  - method: Configure the method to use for Anomaly Detection. Options are [threshold]
- root_cause_analysis:
  - method: Configure the method to use for Root Cause Analysis. Options are [simple, hierarchical]
  - summary_size: The number of top-k dimensions to present in the final result
  - output_topic: (Optional) Set the name of the output topic. Defaults to `{input_topic}-{method}-out`