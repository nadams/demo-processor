CREATE TABLE render.status(
  id INT NOT NULL,
  value VARCHAR(32) NOT NULL,

  CONSTRAINT pk_status PRIMARY KEY (id),
  CONSTRAINT uq_status_value UNIQUE (value)
);

INSERT INTO render.status VALUES
(1, 'Created'),
(2, 'Rendering'),
(3, 'Dead'),
(4, 'Finished');

CREATE TABLE render.demo(
  id SERIAL NOT NULL,
  process_id CHAR(36) NOT NULL,
  status INT NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL,
  completed TIMESTAMP WITH TIME ZONE NULL,

  CONSTRAINT pk_demo PRIMARY KEY (id),
  CONSTRAINT uq_demo_process_id UNIQUE (process_id),
  CONSTRAINT fk_demo_status FOREIGN KEY (status) REFERENCES render.status(id)
);

CREATE TABLE render.demo_data(
  id INT NOT NULL,
  data BYTEA NOT NULL,

  CONSTRAINT pk_demo_data PRIMARY KEY (id),
  CONSTRAINT fk_demo_data_demo FOREIGN KEY (id) REFERENCES render.demo(id)
);

CREATE TABLE render.wad(
  id SERIAL NOT NULL,
  filename VARCHAR(255) NOT NULL,
  checksum CHAR(32) NOT NULL,

  CONSTRAINT pk_wad PRIMARY KEY (id),
  CONSTRAINT uq_wad UNIQUE (filename, checksum)
);

CREATE TABLE render.demo_wad(
  demo_id INT NOT NULL,
  wad_id INT NOT NULL,

  CONSTRAINT pk_demo_wad PRIMARY KEY (demo_id, wad_id),
  CONSTRAINT fk_demo_wad_demo FOREIGN KEY (demo_id) REFERENCES render.demo(id),
  CONSTRAINT fk_demo_wad_wad FOREIGN KEY (wad_id) REFERENCES render.wad(id)
);
