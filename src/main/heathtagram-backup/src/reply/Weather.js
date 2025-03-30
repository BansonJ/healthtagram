import React, { useState, useEffect } from 'react';
import axios from 'axios';

function Weather() {
  const [weather, setWeather] = useState([]);
  const [loading, setLoading] = useState(true);

  // Fetch weather data when the component mounts
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/crawling") // Adjust the URL to your backend if different
      .then((response) => {
        setWeather(response.data); // Assuming the response is an array with weather data
        setLoading(false); // Set loading to false once data is fetched
      })
      .catch((error) => {
        console.error("Error fetching weather data:", error);
        setLoading(false);
      });
  }, []);

  // Function to group the weather times into two per line
  const groupTimesInPairs = (times) => {
    const pairs = [];
    for (let i = 1; i < times.length; i += 2) {
      pairs.push(times.slice(i, i + 2)); // Groups two elements per line
    }
    return pairs;
  };

  return (
    <div className="App">
      <h1>Weather Information</h1>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div>
          {/* Displaying the first element of time */}
          <h3>{weather[0]}</h3>

          {/* Displaying the rest of the times in pairs */}
          {groupTimesInPairs(weather).map((pair, index) => (
            <p key={index}>
              {pair.join(" | ")} {/* Displaying each pair separated by a pipe for clarity */}
            </p>
          ))}
        </div>
      )}
    </div>
  );
}

export default Weather;
