
package com.example.library.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

@Service

public class ImageService {

	public static String UPLOAD_ROOT = "upload-dir";

	private final ResourceLoader resourceLoader;

	Logger logger = LoggerFactory.getLogger(ImageService.class);

	@Autowired
	private ImageRepository imageRepository;

	public ImageService(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Flux<Image> findAllImages() {

		return imageRepository.findAll().log("findAll");
	}

	public Mono<Resource> findOneImage(String filename) {
		return Mono.fromSupplier(() -> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename))
				.log("findOneImage");
	}

	public Mono<Void> createImage(Flux<FilePart> files) {

		return files.log("createImage-files").flatMap(file -> {

			if (!file.filename().isBlank()) {
				Mono<Image> saveDatabaseImage = imageRepository
						.save(new Image(UUID.randomUUID().toString(), file.filename())).log("createImage-save");

				Mono<Void> copyFile = Mono.just(Paths.get(UPLOAD_ROOT, file.filename()).toFile())
						.log("createImage-picktarget").map(destFile -> {
							try {
								destFile.createNewFile();
								return destFile;
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}).log("createImage-newfile").flatMap(file::transferTo).log("createImage-copy");
				return Mono.when(saveDatabaseImage, copyFile).log("createImage-when");
			} else {
				return Mono.empty();
			}

		}).log("createImage-flatMap").then().log("createImage-done");

	}

	public Mono<Void> deleteImage(String filename) {
		Mono<Void> deleteDatabaseImage = imageRepository.findByName(filename).log("deleteImage-find")
				.flatMap(imageRepository::delete).log("deleteImage-record");

		Mono<Object> deleteFile = Mono.fromRunnable(() -> {
			try {
				Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).log("deleteImage-file");

		return Mono.when(deleteDatabaseImage, deleteFile).log("deleteImage-when").then().log("deleteImage-done");
	}

}
