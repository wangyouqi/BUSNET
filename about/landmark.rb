require "mysql2"
require "csv"
client = Mysql2::Client.new(:host =>'localhost',:username => 'root',:password => '',:database => 'bus4')
sql = "select * from landmark"
result = client.query(sql)
arr = []
CSV.open("landmark.csv","w") do |row|
    
  result.each do |var|
    var.each do |key,value|
      p var.class
      arr << value
    end
    row.puts(arr)
    arr = []
  end
end
