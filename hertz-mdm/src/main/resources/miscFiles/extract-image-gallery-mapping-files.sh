for year in `seq 2008 2018`
do
   for country in US CA
   do
       sudo -u infact unzip ${country}_ImageGalleryMapping_${year}.zip '*_Vehicle.txt' -d NVD_Fleet_${country}_EN_${year}
   done
done

