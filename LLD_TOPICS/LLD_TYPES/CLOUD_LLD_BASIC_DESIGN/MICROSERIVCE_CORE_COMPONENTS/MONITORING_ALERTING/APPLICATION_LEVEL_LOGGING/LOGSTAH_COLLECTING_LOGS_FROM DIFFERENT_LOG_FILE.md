Yes, **Logstash** can collect logs from multiple log files and send them to **Elasticsearch** for indexing and searching. If you have 10 log files generated by your application (e.g., from 10 different services or multiple instances of the same service), Logstash can be configured to monitor these log files, collect their contents, and forward them to **Elasticsearch**.

Here’s how it works:

### How Logstash Collects Logs from Multiple Files

1. **File Input Plugin**: Logstash uses the **File input plugin** to read log files. This plugin allows Logstash to monitor files, including multiple log files, in a specified directory or set of directories.

2. **Pattern Matching**: You can use pattern matching in the Logstash configuration file to specify which log files to monitor. This way, you can collect logs from multiple files, even if they are in different directories.

3. **Filter Plugins**: Logstash can apply various **filter plugins** (e.g., **grok**, **date**, **mutate**) to parse and transform the log data, ensuring it is structured in a way that is suitable for indexing in Elasticsearch.

4. **Elasticsearch Output Plugin**: Once Logstash processes the logs, it sends the processed data to **Elasticsearch** using the **Elasticsearch output plugin**. The logs are indexed into Elasticsearch, where they can be queried and visualized.

### Logstash Configuration Example

Here's an example of how to configure **Logstash** to read from multiple log files and send the logs to **Elasticsearch**:

```plaintext
input {
    file {
        path => ["/var/log/service1/*.log", "/var/log/service2/*.log", "/var/log/service3/*.log"]
        start_position => "beginning"  # Optional: Starts reading from the beginning of the log file
        sincedb_path => "/dev/null"   # Optional: Makes sure Logstash always processes all logs
    }
}

filter {
    # Add any filters here, such as grok parsing, date formatting, etc.
    # Example: Parse log lines into structured data
    grok {
        match => { "message" => "%{TIMESTAMP_ISO8601:timestamp} %{WORD:log_level} %{GREEDYDATA:log_message}" }
    }
}

output {
    elasticsearch {
        hosts => ["http://localhost:9200"]
        index => "logs-%{+YYYY.MM.dd}"  # Index name with date suffix
    }
}
```

### Explanation of the Configuration:

- **input**: The `file` input plugin is configured with a `path` that specifies the location of the log files. In this case, it will monitor logs from three different directories (`service1`, `service2`, `service3`).

  - **path**: A list of paths to log files. Logstash will read all files in these directories that match the pattern (`*.log` in this example).
  - **start_position**: This is set to `beginning` to ensure Logstash reads from the start of the log files, especially useful when initially setting up Logstash to process all logs.
  - **sincedb_path**: This controls how Logstash tracks the state of files. By setting it to `/dev/null`, Logstash will re-read the logs every time it starts. In production, this should be set to a file to keep track of the reading position.

- **filter**: The `grok` filter is used to parse each log entry. You can use **grok** patterns to match the structure of your logs and extract meaningful fields from the raw log data.

  - In the example, it extracts `timestamp`, `log_level`, and `log_message` from the log entries.

- **output**: The `elasticsearch` output plugin sends the parsed and structured log data to Elasticsearch.
  - **hosts**: Specifies the Elasticsearch server where the logs should be sent.
  - **index**: Defines the Elasticsearch index pattern. In this case, it creates daily indices with a format like `logs-2025.04.01`.

### Workflow:

1. **Logstash** monitors the log files at the specified paths.
2. As new logs are generated, Logstash reads them and applies any **filters** (e.g., grok) to extract and structure the log data.
3. The structured log data is then sent to **Elasticsearch**.
4. Elasticsearch indexes the data, and you can use **Kibana** or any other Elasticsearch client to query, analyze, and visualize the logs.

### Key Considerations:

- **Log File Rotation**: If your logs rotate (e.g., daily), Logstash will continue reading from the new files as long as they match the path pattern you specify.
- **Performance**: When you have many log files (e.g., in a distributed system with multiple services), it is important to ensure that Logstash is properly optimized for reading multiple files. You may need to adjust the settings for parallel file reading and filtering.
- **Log Format**: Make sure that all log files follow a consistent structure if you're applying the same parsing rules across them. If the logs differ in format, you may need to use conditionals in the Logstash config to handle different log formats.

### In Summary:

- **Logstash** is fully capable of collecting logs from multiple log files, processing them, and sending them to **Elasticsearch** for storage and indexing.
- The logs from multiple services or microservices can be collected, parsed, and stored in a centralized Elasticsearch instance, making it easier to query and visualize them in **Kibana**.
- This approach helps in managing and analyzing logs from a large-scale system or distributed architecture, especially in microservices environments.

If you need more specifics on any of the Logstash components or further clarification, feel free to ask!
