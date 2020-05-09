// Tomcat（Raspberry Pi）のIPアドレス
const URL = 'http://192.168.3.6';

// Tomcat（Raspberry Pi）のポート番号
const PORT = '8080';

// 開始地点の緯度、経度
START_LAT_LNG = {
    lat : 35.596435,
    lng : 139.737115
};

const START_HEADING = 0; // 開始時の方角

let reload_time = 2000; // Raspberry Piからデータを取得する間隔（ミリ秒）

let panorama;
let previous_id = ""; // 前回のREST通信で取得したデータに含まれるid

/**
 * Top画面のローディング時に呼び出され、初期化を行う。
 * @returns
 */
function init() {
    console.log("init");

    // Top画面のテキストボックスから緯度と経度を取得し、コンソールに表示する。（デバッグ用）
    const latlang = $("#lat-lng").val();
    console.log(latlang)

    // ストリートビューをTop画面に表示する。
    panorama = new google.maps.StreetViewPanorama(document
            .getElementById('street-view'), {
        position : START_LAT_LNG,
        pov : {
            heading : START_HEADING,
            pitch : 0
        },
        zoom : 1
    });
}

/**
 * 「ストリートビューを表示」ボタンが押下された時に、
 * テキストボックスから取得した緯度/経度で地図を再描画する。
 * @returns
 */
function reload() {
    console.log("reload");

    // テキストボックスから緯度と経度を取得する。
    const latlang = $("#lat-lng").val();
    console.log(latlang)

    const new_lat = latlang.split(",")[0].trim();
    const new_lng = latlang.split(",")[1].trim();

    // 経度と緯度をセットする。
    START_LAT_LNG = {
        lat : Number(new_lat),
        lng : Number(new_lng)
    };

    // ストリートビューを表示する。
    panorama = new google.maps.StreetViewPanorama(document
            .getElementById('street-view'), {
        position : START_LAT_LNG,
        pov : {
            heading : START_HEADING,
            pitch : 0
        },
        zoom : 1
    });
}

/**
 * ストリートビューを直進する。
 * @returns
 */
function goStraight() {

    let Links
    Links = panorama.getLinks();

    //現在向ている方向に近いlinkを選択する。
    let val = 360;
    let currentPov = panorama.getPov();
    Links.forEach(function(element, index) {
        let ans = Math.abs(currentPov.heading - element.heading);
        if (val > ans) {
            val = ans;
            target = index;
        }
    });

    // 次に移動するLink先に向きを変える
    panorama.setPov({
        heading : Links[target].heading,
        pitch : 0
    });
    // 次のストリートビューに移動する
    panorama.setPano(Links[target]['pano']);

    let latlng

    // (緯度, 経度)のフォーマットとして取得する
    latlng = panorama.getPosition() + "";

    // (緯度, 経度)の ( と ) を除去する
    latlng = latlng.slice(1, latlng.length - 1);

    // テキストボックスに緯度と経度を表示する
    $("#lat-lng").val(latlng);
}

/**
 * Top画面のonloadで呼び出され、reload_timeで指定したミリ秒毎に
 * Raspberry Piからデータ（ペダルを漕いだ証）を取得する。
 * @returns
 */
function getData() {
    var reload = function() {

        // Raspberry Piにデプロイしたアプリの通信先URI
        const url = URL + ":" + PORT + "/cycling";

        $.get(url).done(function(message, textStatus, jqXHR) {
            // 取得したHTTPレスポンスのデータをJSON文字列に変換する。
            id_time = JSON.stringify(message);

            // HTTPレスポンスの文字列からid部分のみ取り出す（時刻部分は捨てる）
            id = id_time.split(',')[0].trim();

            // id部分が前回の通信結果と異なれば自転車を前進させる
            if (previous_id != id) {
                goStraight();
                console.log(id_time); // デバッグ表示
            }
            previous_id = id;

        })
        // 通信に失敗した時に実行される
        .fail(function(jqXHR, textStatus, errorThrown) {
            console.log("REST通信失敗：サイクリングデータを取得できませんでした。");
        })
        // alwaysは、成功/失敗に関わらず実行される
        .always(function() {
            //console.log("always");
        });
    }
    setInterval(reload, reload_time);

}
