import React from "react";

const TunaImage = () => {
  const handleTunaClick = () => {
    
    const audio = document.getElementById("tuna-audio");
    audio.currentTime = 0;
    audio.play();

    const tunaImage = document.getElementById("tuna-image");
    tunaImage.style.animation = "none";
    setTimeout(() => {
      tunaImage.style.animation = "spin 2s linear";
    }, 10);

    makeFishSwim();
  };

  const makeFishSwim = () => {
    const animations = ["swim-left", "swim-right", "swim-diagonal", "swim-bottom"];
    let tunasCompleted = 0;
    let linkOpened = false;

    animations.forEach((animation, i) => {
      setTimeout(() => {
        let tuna = document.createElement("div");
        tuna.innerHTML = "🐟";
        tuna.className = "tuna";
        tuna.style.animation = `${animation} 5s linear forwards`;
        document.body.appendChild(tuna);

        setTimeout(() => {
          tuna.innerHTML = `<img src="/assets/fish-skeleton.png" width="80px" alt="Fish Skeleton">`;
        }, 2500);

        setTimeout(() => {
          tuna.remove();
          tunasCompleted++;

          if (tunasCompleted === 4 && !linkOpened) {
            linkOpened = true;
            //window.open("https://www.youtube.com/watch?v=jIQ6UV2onyI", "_blank");
          }
        }, 5000);
      }, i * 500);
    });
  };

  return (
    <img
      src="/assets/tuna.jpg"
      alt="Tuna"
      id="tuna-image"
      className="tuna-image"
      onClick={handleTunaClick}
    />
  );
};

export default TunaImage;
