package com.example.mapintegrateopenstree

import android.content.Context.MODE_PRIVATE
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mapintegrateopenstree.databinding.FragmentMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null

    private val binding get() = _binding!!

    private lateinit var mMap: MapView

    private lateinit var _controller: IMapController

    private lateinit var mMyLocationOverlay: MyLocationNewOverlay

    private var _lonLat: String? = null
    private val lonLat get() = _lonLat!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            requireActivity().applicationContext,
            requireActivity().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMapBinding.inflate(layoutInflater, container, false)
//        backPressedHandle {
//            requireActivity().finishAffinity()
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeMap()

        initializeListener()

    }

    private fun initializeListener() {
        binding.getLocationBtn.setOnClickListener {
            binding.location.text = lonLat
        }
    }

    private fun initializeMap() {
        binding.osmMap.apply {

            //disable multiple map
            disableMultiMap()

            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            getLocalVisibleRect(Rect())
            mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), this)
            _controller = controller
            mMyLocationOverlay.apply {
                enableMyLocation()
                enableFollowLocation()
                isDrawAccuracyEnabled = true
                controller.setZoom(6.0)

                addMapListener(object : MapListener {
                    override fun onScroll(event: ScrollEvent?): Boolean {
                        binding.osmMap.overlays.clear()
                        val startPoint = GeoPoint(
                            event?.source?.mapCenter?.latitude ?: 0.0,
                            event?.source?.mapCenter?.longitude ?: 0.0
                        )
                        val startMarker = Marker(binding.osmMap)
                        startMarker.position = startPoint
                        startMarker.icon =
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_location)
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        binding.osmMap.overlays.add(startMarker)
                        _lonLat =
                            "la ${event?.source?.mapCenter?.latitude} || lo ${event?.source?.mapCenter?.longitude}"
                        Log.e("TAG", "onCreate:la ${event?.source?.mapCenter?.latitude}")
                        Log.e("TAG", "onCreate:lo ${event?.source?.mapCenter?.longitude}")
                        return true
                    }

                    override fun onZoom(event: ZoomEvent?): Boolean {
                        Log.e(
                            "TAG",
                            "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}"
                        )
                        return true
                    }
                })

            }
        }
    }

    private fun MapView.disableMultiMap() {
        setScrollableAreaLimitDouble(BoundingBox(85.0, 180.0, -85.0, -180.0))
        maxZoomLevel = 20.0
        minZoomLevel = 4.0
        isHorizontalMapRepetitionEnabled = false
        isVerticalMapRepetitionEnabled = false
        setScrollableAreaLimitLatitude(
            MapView.getTileSystem().maxLatitude, MapView.getTileSystem().minLatitude, 0
        )
    }
}